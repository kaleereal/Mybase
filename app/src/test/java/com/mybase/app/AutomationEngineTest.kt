package com.mybase.app.domain.automation

import com.mybase.app.data.dao.*
import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.field.EntryManager
import com.mybase.app.domain.model.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AutomationEngineTest {

    private lateinit var mockRepository: MyBaseRepository
    private lateinit var entryManager: EntryManager
    private lateinit var automationEngine: AutomationEngine
    private val entriesMap = mutableMapOf<String, EntryEntity>()
    private val automations = mutableListOf<AutomationEntity>()

    @Before
    fun setup() {
        val entryDao = object : EntryDao {
            override fun getEntriesByLibrary(libraryId: String) = flowOf(entriesMap.values.toList())
            override suspend fun getEntriesByLibraryList(libraryId: String) = entriesMap.values.toList()
            override suspend fun getEntryById(id: String) = entriesMap[id]
            override suspend fun getAllEntriesList() = entriesMap.values.toList()
            override suspend fun insertEntry(entry: EntryEntity) { entriesMap[entry.id] = entry }
            override suspend fun insertEntries(entriesList: List<EntryEntity>) { entriesList.forEach { entriesMap[it.id] = it } }
            override suspend fun updateEntry(entry: EntryEntity) { entriesMap[entry.id] = entry }
            override suspend fun deleteEntry(entry: EntryEntity) { entriesMap.remove(entry.id) }
            override suspend fun deleteEntriesByIds(entryIds: List<String>) { entryIds.forEach { entriesMap.remove(it) } }
        }

        val automationDao = object : AutomationDao {
            override fun getAutomationsByLibrary(libraryId: String) = flowOf(automations.filter { it.libraryId == libraryId })
            override suspend fun getActiveAutomationsByLibrary(libraryId: String) =
                automations.filter { it.libraryId == libraryId && it.isEnabled }

            override suspend fun insertAutomation(automation: AutomationEntity) { automations.add(automation) }
            override suspend fun updateAutomation(automation: AutomationEntity) {}
            override suspend fun deleteAutomation(automation: AutomationEntity) {}
        }

        val dummyWorkspaceDao = object : WorkspaceDao {
            override fun getAllWorkspaces() = flowOf(emptyList<WorkspaceEntity>())
            override suspend fun getWorkspaceById(id: String) = null
            override suspend fun insertWorkspace(workspace: WorkspaceEntity) {}
            override suspend fun updateWorkspace(workspace: WorkspaceEntity) {}
            override suspend fun deleteWorkspace(workspace: WorkspaceEntity) {}
        }
        val dummyLibraryDao = object : LibraryDao {
            override fun getLibrariesByWorkspace(workspaceId: String) = flowOf(emptyList<LibraryEntity>())
            override suspend fun getLibraryById(id: String) = null
            override suspend fun getAllLibrariesList() = emptyList<LibraryEntity>()
            override suspend fun insertLibrary(library: LibraryEntity) {}
            override suspend fun updateLibrary(library: LibraryEntity) {}
            override suspend fun deleteLibrary(library: LibraryEntity) {}
        }
        val dummyFieldDao = object : FieldDao {
            override fun getFieldsByLibrary(libraryId: String) = flowOf(emptyList<FieldEntity>())
            override suspend fun getFieldsByLibraryList(libraryId: String) = emptyList<FieldEntity>()
            override suspend fun getFieldById(id: String) = null
            override suspend fun insertField(field: FieldEntity) {}
            override suspend fun updateField(field: FieldEntity) {}
            override suspend fun deleteField(field: FieldEntity) {}
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
            dummyWorkspaceDao, dummyLibraryDao, dummyFieldDao, entryDao, dummyRelationDao,
            dummyViewDao, automationDao, dummyDashboardWidgetDao, dummyAttachmentDao
        )

        entryManager = EntryManager(mockRepository)
        automationEngine = AutomationEngine(mockRepository, entryManager)
    }

    @Test
    fun testAutomationTriggerUpdateField() = runBlocking {
        val entry = EntryEntity("e1", "lib1", "{\"f_status\":\"Pending\"}")
        entriesMap[entry.id] = entry

        automations.add(
            AutomationEntity(
                id = "auto1",
                libraryId = "lib1",
                name = "Auto Complete",
                triggerType = TriggerType.ENTRY_CREATED,
                actionType = ActionType.UPDATE_FIELD,
                actionConfigJson = "{\"targetFieldId\":\"f_status\", \"newValue\":\"Completed\"}"
            )
        )

        automationEngine.triggerAutomations(TriggerType.ENTRY_CREATED, entry)

        val updated = entriesMap["e1"]
        assertNotNull(updated)
        assertTrue(updated!!.fieldValuesJson.contains("Completed"))
    }
}
