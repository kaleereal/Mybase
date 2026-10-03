package com.mybase.app.domain.backup

import com.mybase.app.data.dao.*
import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.model.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class BackupRestoreTest {

    private lateinit var mockRepository: MyBaseRepository
    private lateinit var backupRestoreManager: BackupRestoreManager

    private val workspaces = mutableMapOf<String, WorkspaceEntity>()
    private val libraries = mutableMapOf<String, LibraryEntity>()
    private val fields = mutableMapOf<String, FieldEntity>()
    private val entriesMap = mutableMapOf<String, EntryEntity>()

    @Before
    fun setup() {
        val workspaceDao = object : WorkspaceDao {
            override fun getAllWorkspaces() = flowOf(workspaces.values.toList())
            override suspend fun getWorkspaceById(id: String) = workspaces[id]
            override suspend fun insertWorkspace(workspace: WorkspaceEntity) { workspaces[workspace.id] = workspace }
            override suspend fun updateWorkspace(workspace: WorkspaceEntity) { workspaces[workspace.id] = workspace }
            override suspend fun deleteWorkspace(workspace: WorkspaceEntity) { workspaces.remove(workspace.id) }
        }

        val libraryDao = object : LibraryDao {
            override fun getLibrariesByWorkspace(workspaceId: String) = flowOf(libraries.values.toList())
            override suspend fun getLibraryById(id: String) = libraries[id]
            override suspend fun getAllLibrariesList() = libraries.values.toList()
            override suspend fun insertLibrary(library: LibraryEntity) { libraries[library.id] = library }
            override suspend fun updateLibrary(library: LibraryEntity) { libraries[library.id] = library }
            override suspend fun deleteLibrary(library: LibraryEntity) { libraries.remove(library.id) }
        }

        val fieldDao = object : FieldDao {
            override fun getFieldsByLibrary(libraryId: String) = flowOf(fields.values.toList())
            override suspend fun getFieldsByLibraryList(libraryId: String) = fields.values.filter { it.libraryId == libraryId }
            override suspend fun getFieldById(id: String) = fields[id]
            override suspend fun insertField(field: FieldEntity) { fields[field.id] = field }
            override suspend fun updateField(field: FieldEntity) { fields[field.id] = field }
            override suspend fun deleteField(field: FieldEntity) { fields.remove(field.id) }
        }

        val entryDao = object : EntryDao {
            override fun getEntriesByLibrary(libraryId: String) = flowOf(entriesMap.values.toList())
            override suspend fun getEntriesByLibraryList(libraryId: String) = entriesMap.values.filter { it.libraryId == libraryId }
            override suspend fun getEntryById(id: String) = entriesMap[id]
            override suspend fun getAllEntriesList() = entriesMap.values.toList()
            override suspend fun insertEntry(entry: EntryEntity) { entriesMap[entry.id] = entry }
            override suspend fun insertEntries(entriesList: List<EntryEntity>) { entriesList.forEach { entriesMap[it.id] = it } }
            override suspend fun updateEntry(entry: EntryEntity) { entriesMap[entry.id] = entry }
            override suspend fun deleteEntry(entry: EntryEntity) { entriesMap.remove(entry.id) }
            override suspend fun deleteEntriesByIds(entryIds: List<String>) { entryIds.forEach { entriesMap.remove(it) } }
        }

        val dummyRelationDao = object : RelationDao {
            override fun getRelationsForEntry(entryId: String) = TODO()
            override suspend fun getRelationsForEntryList(entryId: String) = emptyList<RelationEntity>()
            override suspend fun getRelationsForFieldAndEntry(fieldId: String, entryId: String) = emptyList<RelationEntity>()
            override suspend fun insertRelation(relation: RelationEntity) {}
            override suspend fun deleteRelation(relation: RelationEntity) {}
            override suspend fun deleteRelationsForFieldAndSourceEntry(fieldId: String, sourceEntryId: String) {}
        }
        val dummyViewDao = object : ViewDao {
            override fun getViewsByLibrary(libraryId: String) = flowOf(emptyList<ViewEntity>())
            override suspend fun getViewsByLibraryList(libraryId: String) = emptyList<ViewEntity>()
            override suspend fun getViewById(id: String) = null
            override suspend fun insertView(view: ViewEntity) {}
            override suspend fun updateView(view: ViewEntity) {}
            override suspend fun deleteView(view: ViewEntity) {}
        }
        val dummyAutomationDao = object : AutomationDao {
            override fun getAutomationsByLibrary(libraryId: String) = flowOf(emptyList<AutomationEntity>())
            override suspend fun getActiveAutomationsByLibrary(libraryId: String) = emptyList<AutomationEntity>()
            override suspend fun insertAutomation(automation: AutomationEntity) {}
            override suspend fun updateAutomation(automation: AutomationEntity) {}
            override suspend fun deleteAutomation(automation: AutomationEntity) {}
        }
        val dummyDashboardWidgetDao = object : DashboardWidgetDao {
            override fun getWidgetsByWorkspace(workspaceId: String) = TODO()
            override suspend fun insertWidget(widget: DashboardWidgetEntity) {}
            override suspend fun updateWidget(widget: DashboardWidgetEntity) {}
            override suspend fun deleteWidget(widget: DashboardWidgetEntity) {}
        }
        val dummyAttachmentDao = object : AttachmentDao {
            override fun getAttachmentsByEntry(entryId: String) = TODO()
            override suspend fun getAttachmentsByEntryList(entryId: String) = emptyList<AttachmentEntity>()
            override suspend fun insertAttachment(attachment: AttachmentEntity) {}
            override suspend fun deleteAttachment(attachment: AttachmentEntity) {}
        }

        mockRepository = MyBaseRepository(
            workspaceDao, libraryDao, fieldDao, entryDao, dummyRelationDao,
            dummyViewDao, dummyAutomationDao, dummyDashboardWidgetDao, dummyAttachmentDao
        )

        backupRestoreManager = BackupRestoreManager(mockRepository)
    }

    @Test
    fun testBackupAndRestoreWorkflow() = runBlocking {
        val ws = WorkspaceEntity("ws1", "Main Base")
        val lib = LibraryEntity("lib1", "ws1", "Movies")
        workspaces[ws.id] = ws
        libraries[lib.id] = lib

        val backupJson = backupRestoreManager.createBackupJson()
        assertTrue(backupJson.contains("Main Base"))
        assertTrue(backupJson.contains("Movies"))

        workspaces.clear()
        libraries.clear()

        val restoreRes = backupRestoreManager.restoreBackupJson(backupJson)
        assertTrue(restoreRes.isSuccess)
        assertEquals(1, workspaces.size)
        assertEquals("Main Base", workspaces["ws1"]?.name)
    }
}
