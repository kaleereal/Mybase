package com.mybase.app.domain.relation

import com.mybase.app.data.dao.*
import com.mybase.app.data.repository.MyBaseRepository
import com.mybase.app.domain.model.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class RelationManagerTest {

    private lateinit var mockRepository: MyBaseRepository
    private lateinit var relationManager: RelationManager

    private val entriesMap = mutableMapOf<String, EntryEntity>()
    private val relations = mutableListOf<RelationEntity>()

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

        val relationDao = object : RelationDao {
            override fun getRelationsForEntry(entryId: String) = TODO()
            override suspend fun getRelationsForEntryList(entryId: String) =
                relations.filter { it.sourceEntryId == entryId || it.targetEntryId == entryId }

            override suspend fun getRelationsForFieldAndEntry(fieldId: String, entryId: String) =
                relations.filter { it.fieldId == fieldId && (it.sourceEntryId == entryId || it.targetEntryId == entryId) }

            override suspend fun insertRelation(relation: RelationEntity) { relations.add(relation) }
            override suspend fun deleteRelation(relation: RelationEntity) { relations.remove(relation) }
            override suspend fun deleteRelationsForFieldAndSourceEntry(fieldId: String, sourceEntryId: String) {
                relations.removeAll { it.fieldId == fieldId && it.sourceEntryId == sourceEntryId }
            }
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
            dummyWorkspaceDao, dummyLibraryDao, dummyFieldDao, entryDao, relationDao,
            dummyViewDao, dummyAutomationDao, dummyDashboardWidgetDao, dummyAttachmentDao
        )

        relationManager = RelationManager(mockRepository)
    }

    @Test
    fun testRelationLinking() = runBlocking {
        val movieEntry = EntryEntity("movie1", "lib_movies", "{}")
        val actor1 = EntryEntity("actor1", "lib_actors", "{}")
        val actor2 = EntryEntity("actor2", "lib_actors", "{}")

        entriesMap[movieEntry.id] = movieEntry
        entriesMap[actor1.id] = actor1
        entriesMap[actor2.id] = actor2

        relationManager.setRelations(
            fieldId = "f_cast",
            sourceLibraryId = "lib_movies",
            targetLibraryId = "lib_actors",
            sourceEntryId = "movie1",
            targetEntryIds = listOf("actor1", "actor2"),
            relationType = RelationType.ONE_TO_MANY
        )

        val linked = relationManager.getLinkedEntriesForEntry("movie1")
        assertEquals(2, linked.size)
        assertTrue(linked.any { it.id == "actor1" })
        assertTrue(linked.any { it.id == "actor2" })
    }
}
