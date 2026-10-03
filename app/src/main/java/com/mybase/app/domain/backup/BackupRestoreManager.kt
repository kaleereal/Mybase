package com.mybase.app.domain.backup

import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.model.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class MyBaseBackupModel(
    val version: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val workspaces: List<WorkspaceEntity>,
    val libraries: List<LibraryEntity>,
    val fields: List<FieldEntity>,
    val entries: List<EntryEntity>,
    val views: List<ViewEntity>,
    val automations: List<AutomationEntity>
)

class BackupRestoreManager(private val repository: MyBaseRepository) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    suspend fun createBackupJson(): String {
        val workspaces = repository.getAllWorkspacesList()
        val libraries = repository.getAllLibrariesList()
        val fields = mutableListOf<FieldEntity>()
        val entries = repository.getAllEntriesList()
        val views = mutableListOf<ViewEntity>()
        val automations = mutableListOf<AutomationEntity>()

        for (lib in libraries) {
            fields.addAll(repository.getFieldsByLibraryList(lib.id))
            views.addAll(repository.getViewsByLibraryList(lib.id))
            automations.addAll(repository.getActiveAutomationsByLibrary(lib.id))
        }

        val backupData = MyBaseBackupModel(
            workspaces = workspaces,
            libraries = libraries,
            fields = fields,
            entries = entries,
            views = views,
            automations = automations
        )

        return json.encodeToString(backupData)
    }

    suspend fun restoreBackupJson(backupJsonString: String): Result<Boolean> {
        return try {
            val backupModel = json.decodeFromString<MyBaseBackupModel>(backupJsonString)

            if (backupModel.version > 1) {
                return Result.failure(IllegalArgumentException("Incompatible backup version ${backupModel.version}"))
            }

            for (ws in backupModel.workspaces) {
                repository.insertWorkspace(ws)
            }
            for (lib in backupModel.libraries) {
                repository.insertLibrary(lib)
            }
            for (field in backupModel.fields) {
                repository.insertField(field)
            }
            for (entry in backupModel.entries) {
                repository.insertEntry(entry)
            }
            for (view in backupModel.views) {
                repository.insertView(view)
            }
            for (auto in backupModel.automations) {
                repository.insertAutomation(auto)
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
