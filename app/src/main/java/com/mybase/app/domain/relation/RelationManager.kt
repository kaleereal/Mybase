package com.mybase.app.domain.relation

import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.model.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class RelationManager(private val repository: MyBaseRepository) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun setRelations(
        fieldId: String,
        sourceLibraryId: String,
        targetLibraryId: String,
        sourceEntryId: String,
        targetEntryIds: List<String>,
        relationType: RelationType
    ) {
        repository.deleteRelationsForFieldAndSourceEntry(fieldId, sourceEntryId)

        for (targetEntryId in targetEntryIds) {
            val relation = RelationEntity(
                id = UUID.randomUUID().toString(),
                fieldId = fieldId,
                sourceLibraryId = sourceLibraryId,
                targetLibraryId = targetLibraryId,
                sourceEntryId = sourceEntryId,
                targetEntryId = targetEntryId,
                relationType = relationType
            )
            repository.insertRelation(relation)
        }

        val sourceEntry = repository.getEntryById(sourceEntryId)
        if (sourceEntry != null) {
            val fieldValues = try {
                json.decodeFromString<Map<String, String>>(sourceEntry.fieldValuesJson).toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()
            }

            fieldValues[fieldId] = json.encodeToString(targetEntryIds)
            repository.updateEntry(sourceEntry.copy(fieldValuesJson = json.encodeToString(fieldValues)))
        }
    }

    suspend fun getLinkedEntriesForEntry(entryId: String): List<EntryEntity> {
        val relations = repository.getRelationsForEntryList(entryId)
        val linkedEntryIds = relations.map {
            if (it.sourceEntryId == entryId) it.targetEntryId else it.sourceEntryId
        }.distinct()

        val allEntries = mutableListOf<EntryEntity>()
        for (id in linkedEntryIds) {
            val entry = repository.getEntryById(id)
            if (entry != null) {
                allEntries.add(entry)
            }
        }
        return allEntries
    }
}
