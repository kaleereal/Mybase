package com.mybase.app.domain.automation

import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.data.DataEngine
import com.mybase.app.domain.field.EntryManager
import com.mybase.app.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class AutomationEngine(
    private val repository: MyBaseRepository,
    private val entryManager: EntryManager
) {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun triggerAutomations(
        triggerType: TriggerType,
        entry: EntryEntity,
        changedFieldId: String? = null
    ) {
        val activeAutomations = repository.getActiveAutomationsByLibrary(entry.libraryId)

        for (automation in activeAutomations) {
            if (automation.triggerType != triggerType) continue

            if (triggerType == TriggerType.FIELD_CHANGED && automation.triggerFieldId != changedFieldId) {
                continue
            }

            executeAction(automation, entry)
        }
    }

    private suspend fun executeAction(automation: AutomationEntity, entry: EntryEntity) {
        when (automation.actionType) {
            ActionType.UPDATE_FIELD -> {
                val config = try {
                    json.decodeFromString<Map<String, String>>(automation.actionConfigJson)
                } catch (e: Exception) {
                    emptyMap()
                }

                val targetFieldId = config["targetFieldId"] ?: return
                val newValue = config["newValue"] ?: ""

                val currentValues = DataEngine.parseValues(entry.fieldValuesJson).toMutableMap()
                currentValues[targetFieldId] = newValue

                val updatedEntry = entry.copy(
                    fieldValuesJson = json.encodeToString(currentValues),
                    updatedAt = System.currentTimeMillis()
                )
                repository.updateEntry(updatedEntry)
            }
            ActionType.CREATE_ENTRY -> {
                val config = try {
                    json.decodeFromString<Map<String, String>>(automation.actionConfigJson)
                } catch (e: Exception) {
                    emptyMap()
                }

                val targetLibraryId = config["targetLibraryId"] ?: entry.libraryId
                entryManager.saveEntry(targetLibraryId, rawValues = emptyMap())
            }
            else -> {}
        }
    }
}
