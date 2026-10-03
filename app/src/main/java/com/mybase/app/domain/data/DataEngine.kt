package com.mybase.app.domain.data

import com.mybase.app.domain.model.*
import kotlinx.serialization.json.*

data class FilterRule(
    val fieldId: String,
    val operator: FilterOperator,
    val value: String = ""
)

data class FilterGroup(
    val rules: List<FilterRule> = emptyList(),
    val logicalOperator: LogicalOperator = LogicalOperator.AND
)

data class SortRule(
    val fieldId: String,
    val isAscending: Boolean = true
)

object DataEngine {

    private val json = Json { ignoreUnknownKeys = true }

    fun filterEntries(
        entries: List<EntryEntity>,
        fieldsMap: Map<String, FieldEntity>,
        filterGroup: FilterGroup?,
        searchQuery: String = ""
    ): List<EntryEntity> {
        return entries.filter { entry ->
            val fieldValuesMap = parseValues(entry.fieldValuesJson)

            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                fieldValuesMap.values.any { valStr ->
                    valStr.contains(searchQuery, ignoreCase = true)
                }
            }

            if (!matchesSearch) return@filter false

            if (filterGroup == null || filterGroup.rules.isEmpty()) {
                return@filter true
            }

            val ruleResults = filterGroup.rules.map { rule ->
                val fieldVal = fieldValuesMap[rule.fieldId] ?: ""
                evaluateRule(fieldVal, rule.operator, rule.value)
            }

            if (filterGroup.logicalOperator == LogicalOperator.AND) {
                ruleResults.all { it }
            } else {
                ruleResults.any { it }
            }
        }
    }

    private fun evaluateRule(fieldValue: String, operator: FilterOperator, ruleValue: String): Boolean {
        return when (operator) {
            FilterOperator.EQUALS -> fieldValue.equals(ruleValue, ignoreCase = true)
            FilterOperator.NOT_EQUALS -> !fieldValue.equals(ruleValue, ignoreCase = true)
            FilterOperator.CONTAINS -> fieldValue.contains(ruleValue, ignoreCase = true)
            FilterOperator.NOT_CONTAINS -> !fieldValue.contains(ruleValue, ignoreCase = true)
            FilterOperator.GREATER_THAN -> {
                val fNum = fieldValue.toDoubleOrNull()
                val rNum = ruleValue.toDoubleOrNull()
                if (fNum != null && rNum != null) fNum > rNum else fieldValue > ruleValue
            }
            FilterOperator.LESS_THAN -> {
                val fNum = fieldValue.toDoubleOrNull()
                val rNum = ruleValue.toDoubleOrNull()
                if (fNum != null && rNum != null) fNum < rNum else fieldValue < ruleValue
            }
            FilterOperator.BETWEEN -> {
                val parts = ruleValue.split(",")
                if (parts.size == 2) {
                    val low = parts[0].trim().toDoubleOrNull()
                    val high = parts[1].trim().toDoubleOrNull()
                    val valNum = fieldValue.toDoubleOrNull()
                    if (low != null && high != null && valNum != null) {
                        valNum in low..high
                    } else false
                } else false
            }
            FilterOperator.IS_EMPTY -> fieldValue.isBlank()
            FilterOperator.IS_NOT_EMPTY -> fieldValue.isNotBlank()
        }
    }

    fun sortEntries(
        entries: List<EntryEntity>,
        sortRules: List<SortRule>
    ): List<EntryEntity> {
        if (sortRules.isEmpty()) return entries

        var comparator: Comparator<EntryEntity>? = null

        for (rule in sortRules) {
            val currentComp = Comparator<EntryEntity> { e1, e2 ->
                val v1 = parseValues(e1.fieldValuesJson)[rule.fieldId] ?: ""
                val v2 = parseValues(e2.fieldValuesJson)[rule.fieldId] ?: ""

                val n1 = v1.toDoubleOrNull()
                val n2 = v2.toDoubleOrNull()

                val res = if (n1 != null && n2 != null) {
                    n1.compareTo(n2)
                } else {
                    v1.compareTo(v2, ignoreCase = true)
                }

                if (rule.isAscending) res else -res
            }

            comparator = if (comparator == null) currentComp else comparator.thenComparing(currentComp)
        }

        return if (comparator != null) entries.sortedWith(comparator) else entries
    }

    fun groupEntries(
        entries: List<EntryEntity>,
        groupFieldId: String
    ): Map<String, List<EntryEntity>> {
        return entries.groupBy { entry ->
            parseValues(entry.fieldValuesJson)[groupFieldId]?.takeIf { it.isNotBlank() } ?: "Unassigned"
        }
    }

    fun parseValues(jsonString: String): Map<String, String> {
        return try {
            json.decodeFromString<Map<String, String>>(jsonString)
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
