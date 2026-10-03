package com.mybase.app.domain.field

import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.model.EntryEntity
import com.mybase.app.domain.model.FieldEntity
import com.mybase.app.domain.model.FieldType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.util.UUID

class EntryManager(private val repository: MyBaseRepository) {

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun saveEntry(
        libraryId: String,
        entryId: String? = null,
        rawValues: Map<String, Any?>
    ): Result<EntryEntity> {
        val fields = repository.getFieldsByLibraryList(libraryId)
        val existingEntry = if (entryId != null) repository.getEntryById(entryId) else null

        val currentValues = if (existingEntry != null) {
            try {
                json.decodeFromString<Map<String, String>>(existingEntry.fieldValuesJson).toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()
            }
        } else {
            mutableMapOf()
        }

        for (field in fields) {
            val inputValue = rawValues[field.id]

            if (field.type == FieldType.AUTO_NUMBER && existingEntry == null) {
                val existingEntries = repository.getEntriesByLibraryList(libraryId)
                val maxNumber = existingEntries.mapNotNull { entry ->
                    val map = try {
                        json.decodeFromString<Map<String, String>>(entry.fieldValuesJson)
                    } catch (e: Exception) { emptyMap() }
                    map[field.id]?.toLongOrNull()
                }.maxOrNull() ?: 0L

                currentValues[field.id] = (maxNumber + 1).toString()
                continue
            }

            if (field.type == FieldType.CREATED_TIME && existingEntry == null) {
                currentValues[field.id] = System.currentTimeMillis().toString()
                continue
            }

            if (field.type == FieldType.MODIFIED_TIME) {
                currentValues[field.id] = System.currentTimeMillis().toString()
                continue
            }

            val formattedValue = FieldTypeHandler.validateAndFormat(field, inputValue)
            if (field.isRequired && (formattedValue == null || formattedValue.isEmpty())) {
                return Result.failure(IllegalArgumentException("Field '${field.name}' is required."))
            }

            if (formattedValue != null) {
                currentValues[field.id] = formattedValue
            }
        }

        val now = System.currentTimeMillis()
        val finalEntry = EntryEntity(
            id = entryId ?: UUID.randomUUID().toString(),
            libraryId = libraryId,
            fieldValuesJson = json.encodeToString(currentValues),
            createdAt = existingEntry?.createdAt ?: now,
            updatedAt = now
        )

        if (existingEntry != null) {
            repository.updateEntry(finalEntry)
        } else {
            repository.insertEntry(finalEntry)
        }

        return Result.success(finalEntry)
    }

    suspend fun deleteEntry(entry: EntryEntity) {
        repository.deleteEntry(entry)
    }

    suspend fun duplicateEntry(entryId: String): EntryEntity? {
        val entry = repository.getEntryById(entryId) ?: return null
        val now = System.currentTimeMillis()
        val newEntry = entry.copy(
            id = UUID.randomUUID().toString(),
            createdAt = now,
            updatedAt = now
        )
        repository.insertEntry(newEntry)
        return newEntry
    }
}
