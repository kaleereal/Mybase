package com.mybase.app.data.repository

import com.mybase.app.data.dao.*
import com.mybase.app.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class MyBaseRepository(
    private val workspaceDao: WorkspaceDao,
    private val libraryDao: LibraryDao,
    private val fieldDao: FieldDao,
    private val entryDao: EntryDao,
    private val relationDao: RelationDao,
    private val viewDao: ViewDao,
    private val automationDao: AutomationDao,
    private val dashboardWidgetDao: DashboardWidgetDao,
    private val attachmentDao: AttachmentDao
) {
    // Workspaces
    fun getAllWorkspaces(): Flow<List<WorkspaceEntity>> = workspaceDao.getAllWorkspaces()
    suspend fun getAllWorkspacesList(): List<WorkspaceEntity> = workspaceDao.getAllWorkspaces().first()
    suspend fun getWorkspaceById(id: String) = workspaceDao.getWorkspaceById(id)
    suspend fun insertWorkspace(workspace: WorkspaceEntity) = workspaceDao.insertWorkspace(workspace)
    suspend fun updateWorkspace(workspace: WorkspaceEntity) = workspaceDao.updateWorkspace(workspace)
    suspend fun deleteWorkspace(workspace: WorkspaceEntity) = workspaceDao.deleteWorkspace(workspace)

    // Libraries
    fun getLibrariesByWorkspace(workspaceId: String): Flow<List<LibraryEntity>> = libraryDao.getLibrariesByWorkspace(workspaceId)
    suspend fun getLibraryById(id: String) = libraryDao.getLibraryById(id)
    suspend fun getAllLibrariesList() = libraryDao.getAllLibrariesList()
    suspend fun insertLibrary(library: LibraryEntity) = libraryDao.insertLibrary(library)
    suspend fun updateLibrary(library: LibraryEntity) = libraryDao.updateLibrary(library)
    suspend fun deleteLibrary(library: LibraryEntity) = libraryDao.deleteLibrary(library)

    // Fields
    fun getFieldsByLibrary(libraryId: String): Flow<List<FieldEntity>> = fieldDao.getFieldsByLibrary(libraryId)
    suspend fun getFieldsByLibraryList(libraryId: String) = fieldDao.getFieldsByLibraryList(libraryId)
    suspend fun getFieldById(id: String) = fieldDao.getFieldById(id)
    suspend fun insertField(field: FieldEntity) = fieldDao.insertField(field)
    suspend fun updateField(field: FieldEntity) = fieldDao.updateField(field)
    suspend fun deleteField(field: FieldEntity) = fieldDao.deleteField(field)

    // Entries
    fun getEntriesByLibrary(libraryId: String): Flow<List<EntryEntity>> = entryDao.getEntriesByLibrary(libraryId)
    suspend fun getEntriesByLibraryList(libraryId: String) = entryDao.getEntriesByLibraryList(libraryId)
    suspend fun getEntryById(id: String) = entryDao.getEntryById(id)
    suspend fun getAllEntriesList() = entryDao.getAllEntriesList()
    suspend fun insertEntry(entry: EntryEntity) = entryDao.insertEntry(entry)
    suspend fun insertEntries(entries: List<EntryEntity>) = entryDao.insertEntries(entries)
    suspend fun updateEntry(entry: EntryEntity) = entryDao.updateEntry(entry)
    suspend fun deleteEntry(entry: EntryEntity) = entryDao.deleteEntry(entry)
    suspend fun deleteEntriesByIds(ids: List<String>) = entryDao.deleteEntriesByIds(ids)

    // Relations
    fun getRelationsForEntry(entryId: String) = relationDao.getRelationsForEntry(entryId)
    suspend fun getRelationsForEntryList(entryId: String) = relationDao.getRelationsForEntryList(entryId)
    suspend fun getRelationsForFieldAndEntry(fieldId: String, entryId: String) = relationDao.getRelationsForFieldAndEntry(fieldId, entryId)
    suspend fun insertRelation(relation: RelationEntity) = relationDao.insertRelation(relation)
    suspend fun deleteRelation(relation: RelationEntity) = relationDao.deleteRelation(relation)
    suspend fun deleteRelationsForFieldAndSourceEntry(fieldId: String, sourceEntryId: String) =
        relationDao.deleteRelationsForFieldAndSourceEntry(fieldId, sourceEntryId)

    // Views
    fun getViewsByLibrary(libraryId: String) = viewDao.getViewsByLibrary(libraryId)
    suspend fun getViewsByLibraryList(libraryId: String) = viewDao.getViewsByLibraryList(libraryId)
    suspend fun getViewById(id: String) = viewDao.getViewById(id)
    suspend fun insertView(view: ViewEntity) = viewDao.insertView(view)
    suspend fun updateView(view: ViewEntity) = viewDao.updateView(view)
    suspend fun deleteView(view: ViewEntity) = viewDao.deleteView(view)

    // Automations
    fun getAutomationsByLibrary(libraryId: String) = automationDao.getAutomationsByLibrary(libraryId)
    suspend fun getActiveAutomationsByLibrary(libraryId: String) = automationDao.getActiveAutomationsByLibrary(libraryId)
    suspend fun insertAutomation(automation: AutomationEntity) = automationDao.insertAutomation(automation)
    suspend fun updateAutomation(automation: AutomationEntity) = automationDao.updateAutomation(automation)
    suspend fun deleteAutomation(automation: AutomationEntity) = automationDao.deleteAutomation(automation)

    // Dashboard Widgets
    fun getWidgetsByWorkspace(workspaceId: String) = dashboardWidgetDao.getWidgetsByWorkspace(workspaceId)
    suspend fun insertWidget(widget: DashboardWidgetEntity) = dashboardWidgetDao.insertWidget(widget)
    suspend fun updateWidget(widget: DashboardWidgetEntity) = dashboardWidgetDao.updateWidget(widget)
    suspend fun deleteWidget(widget: DashboardWidgetEntity) = dashboardWidgetDao.deleteWidget(widget)

    // Attachments
    fun getAttachmentsByEntry(entryId: String) = attachmentDao.getAttachmentsByEntry(entryId)
    suspend fun getAttachmentsByEntryList(entryId: String) = attachmentDao.getAttachmentsByEntryList(entryId)
    suspend fun insertAttachment(attachment: AttachmentEntity) = attachmentDao.insertAttachment(attachment)
    suspend fun deleteAttachment(attachment: AttachmentEntity) = attachmentDao.deleteAttachment(attachment)
}
