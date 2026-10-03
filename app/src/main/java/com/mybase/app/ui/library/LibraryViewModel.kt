package com.mybase.app.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.field.EntryManager
import com.mybase.app.domain.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class LibraryViewModel(
    private val repository: MyBaseRepository,
    private val entryManager: EntryManager
) : ViewModel() {

    private val _selectedLibraryId = MutableStateFlow<String?>(null)
    val selectedLibraryId: StateFlow<String?> = _selectedLibraryId.asStateFlow()

    fun selectLibrary(libraryId: String) {
        _selectedLibraryId.value = libraryId
    }

    fun getLibrariesByWorkspace(workspaceId: String): Flow<List<LibraryEntity>> =
        repository.getLibrariesByWorkspace(workspaceId)

    fun getFieldsByLibrary(libraryId: String): Flow<List<FieldEntity>> =
        repository.getFieldsByLibrary(libraryId)

    fun getEntriesByLibrary(libraryId: String): Flow<List<EntryEntity>> =
        repository.getEntriesByLibrary(libraryId)

    fun createLibrary(workspaceId: String, name: String, description: String = "", icon: String = "grid_on", color: String = "#03DAC6") {
        viewModelScope.launch {
            val libraryId = UUID.randomUUID().toString()
            val library = LibraryEntity(
                id = libraryId,
                workspaceId = workspaceId,
                name = name,
                description = description,
                icon = icon,
                color = color
            )
            repository.insertLibrary(library)

            val titleField = FieldEntity(
                id = UUID.randomUUID().toString(),
                libraryId = libraryId,
                name = "Title",
                type = FieldType.TEXT,
                order = 0,
                isVisible = true,
                isRequired = true
            )
            repository.insertField(titleField)

            val defaultView = ViewEntity(
                id = UUID.randomUUID().toString(),
                libraryId = libraryId,
                name = "All Entries",
                type = ViewType.LIST,
                isDefault = true
            )
            repository.insertView(defaultView)
        }
    }

    fun updateLibrary(library: LibraryEntity) {
        viewModelScope.launch {
            repository.updateLibrary(library.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteLibrary(library: LibraryEntity) {
        viewModelScope.launch {
            repository.deleteLibrary(library)
        }
    }

    fun createField(
        libraryId: String,
        name: String,
        type: FieldType,
        isRequired: Boolean = false,
        optionsJson: String = "{}"
    ) {
        viewModelScope.launch {
            val fields = repository.getFieldsByLibraryList(libraryId)
            val field = FieldEntity(
                id = UUID.randomUUID().toString(),
                libraryId = libraryId,
                name = name,
                type = type,
                order = fields.size,
                isVisible = true,
                isRequired = isRequired,
                optionsJson = optionsJson
            )
            repository.insertField(field)
        }
    }

    fun updateField(field: FieldEntity) {
        viewModelScope.launch {
            repository.updateField(field.copy(updatedAt = System.currentTimeMillis()))
        }
    }

    fun deleteField(field: FieldEntity) {
        viewModelScope.launch {
            repository.deleteField(field)
        }
    }

    fun reorderFields(fields: List<FieldEntity>) {
        viewModelScope.launch {
            fields.forEachIndexed { index, field ->
                repository.updateField(field.copy(order = index, updatedAt = System.currentTimeMillis()))
            }
        }
    }

    fun saveEntry(
        libraryId: String,
        entryId: String? = null,
        rawValues: Map<String, Any?>,
        onResult: (Result<EntryEntity>) -> Unit
    ) {
        viewModelScope.launch {
            val result = entryManager.saveEntry(libraryId, entryId, rawValues)
            onResult(result)
        }
    }

    fun deleteEntry(entry: EntryEntity) {
        viewModelScope.launch {
            entryManager.deleteEntry(entry)
        }
    }

    fun duplicateEntry(entryId: String) {
        viewModelScope.launch {
            entryManager.duplicateEntry(entryId)
        }
    }
}
