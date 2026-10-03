package com.mybase.app.domain.field

import com.mybase.app.domain.model.FieldEntity
import com.mybase.app.domain.model.FieldType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object FieldTypeHandler {

    private val json = Json { ignoreUnknownKeys = true }

    fun validateAndFormat(field: FieldEntity, rawInput: Any?): String? {
        if (field.isRequired) {
            if (rawInput == null || rawInput.toString().trim().isEmpty()) {
                return null
            }
        }

        if (rawInput == null) return ""

        return when (field.type) {
            FieldType.NUMBER, FieldType.AUTO_NUMBER -> {
                val str = rawInput.toString().trim()
                if (str.isEmpty()) "" else str.toLongOrNull()?.toString() ?: str
            }
            FieldType.DECIMAL, FieldType.CURRENCY, FieldType.PERCENTAGE, FieldType.RATING -> {
                val str = rawInput.toString().trim()
                if (str.isEmpty()) "" else str.toDoubleOrNull()?.toString() ?: str
            }
            FieldType.CHECKBOX -> {
                when (rawInput) {
                    is Boolean -> rawInput.toString()
                    else -> rawInput.toString().lowercase() == "true"
                }.toString()
            }
            FieldType.MULTIPLE_CHOICE, FieldType.MULTIPLE_RELATION -> {
                when (rawInput) {
                    is List<*> -> json.encodeToString(rawInput.map { it.toString() })
                    is String -> if (rawInput.startsWith("[")) rawInput else json.encodeToString(listOf(rawInput))
                    else -> json.encodeToString(listOf(rawInput.toString()))
                }
            }
            else -> rawInput.toString()
        }
    }

    fun parseFieldValue(field: FieldEntity, valueString: String?): Any? {
        if (valueString.isNullOrEmpty()) return null

        return when (field.type) {
            FieldType.NUMBER, FieldType.AUTO_NUMBER -> valueString.toLongOrNull()
            FieldType.DECIMAL, FieldType.CURRENCY, FieldType.PERCENTAGE, FieldType.RATING -> valueString.toDoubleOrNull()
            FieldType.CHECKBOX -> valueString.toBooleanStrictOrNull() ?: false
            FieldType.MULTIPLE_CHOICE, FieldType.MULTIPLE_RELATION -> {
                try {
                    json.decodeFromString<List<String>>(valueString)
                } catch (e: Exception) {
                    listOf(valueString)
                }
            }
            else -> valueString
        }
    }
}
