package com.mybase.app.data.dao

import androidx.room.*
import com.mybase.app.domain.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkspaceDao {
    @Query("SELECT * FROM workspaces ORDER BY updatedAt DESC")
    fun getAllWorkspaces(): Flow<List<WorkspaceEntity>>

    @Query("SELECT * FROM workspaces WHERE id = :id")
    suspend fun getWorkspaceById(id: String): WorkspaceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkspace(workspace: WorkspaceEntity)

    @Update
    suspend fun updateWorkspace(workspace: WorkspaceEntity)

    @Delete
    suspend fun deleteWorkspace(workspace: WorkspaceEntity)
}

@Dao
interface LibraryDao {
    @Query("SELECT * FROM libraries WHERE workspaceId = :workspaceId ORDER BY updatedAt DESC")
    fun getLibrariesByWorkspace(workspaceId: String): Flow<List<LibraryEntity>>

    @Query("SELECT * FROM libraries WHERE id = :id")
    suspend fun getLibraryById(id: String): LibraryEntity?

    @Query("SELECT * FROM libraries")
    suspend fun getAllLibrariesList(): List<LibraryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLibrary(library: LibraryEntity)

    @Update
    suspend fun updateLibrary(library: LibraryEntity)

    @Delete
    suspend fun deleteLibrary(library: LibraryEntity)
}

@Dao
interface FieldDao {
    @Query("SELECT * FROM fields WHERE libraryId = :libraryId ORDER BY `order` ASC")
    fun getFieldsByLibrary(libraryId: String): Flow<List<FieldEntity>>

    @Query("SELECT * FROM fields WHERE libraryId = :libraryId ORDER BY `order` ASC")
    suspend fun getFieldsByLibraryList(libraryId: String): List<FieldEntity>

    @Query("SELECT * FROM fields WHERE id = :id")
    suspend fun getFieldById(id: String): FieldEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertField(field: FieldEntity)

    @Update
    suspend fun updateField(field: FieldEntity)

    @Delete
    suspend fun deleteField(field: FieldEntity)
}

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries WHERE libraryId = :libraryId ORDER BY updatedAt DESC")
    fun getEntriesByLibrary(libraryId: String): Flow<List<EntryEntity>>

    @Query("SELECT * FROM entries WHERE libraryId = :libraryId ORDER BY updatedAt DESC")
    suspend fun getEntriesByLibraryList(libraryId: String): List<EntryEntity>

    @Query("SELECT * FROM entries WHERE id = :id")
    suspend fun getEntryById(id: String): EntryEntity?

    @Query("SELECT * FROM entries")
    suspend fun getAllEntriesList(): List<EntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: EntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntries(entries: List<EntryEntity>)

    @Update
    suspend fun updateEntry(entry: EntryEntity)

    @Delete
    suspend fun deleteEntry(entry: EntryEntity)

    @Query("DELETE FROM entries WHERE id IN (:entryIds)")
    suspend fun deleteEntriesByIds(entryIds: List<String>)
}

@Dao
interface RelationDao {
    @Query("SELECT * FROM relations WHERE sourceEntryId = :entryId OR targetEntryId = :entryId")
    fun getRelationsForEntry(entryId: String): Flow<List<RelationEntity>>

    @Query("SELECT * FROM relations WHERE sourceEntryId = :entryId OR targetEntryId = :entryId")
    suspend fun getRelationsForEntryList(entryId: String): List<RelationEntity>

    @Query("SELECT * FROM relations WHERE fieldId = :fieldId AND (sourceEntryId = :entryId OR targetEntryId = :entryId)")
    suspend fun getRelationsForFieldAndEntry(fieldId: String, entryId: String): List<RelationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(relation: RelationEntity)

    @Delete
    suspend fun deleteRelation(relation: RelationEntity)

    @Query("DELETE FROM relations WHERE fieldId = :fieldId AND sourceEntryId = :sourceEntryId")
    suspend fun deleteRelationsForFieldAndSourceEntry(fieldId: String, sourceEntryId: String)
}

@Dao
interface ViewDao {
    @Query("SELECT * FROM views WHERE libraryId = :libraryId")
    fun getViewsByLibrary(libraryId: String): Flow<List<ViewEntity>>

    @Query("SELECT * FROM views WHERE libraryId = :libraryId")
    suspend fun getViewsByLibraryList(libraryId: String): List<ViewEntity>

    @Query("SELECT * FROM views WHERE id = :id")
    suspend fun getViewById(id: String): ViewEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertView(view: ViewEntity)

    @Update
    suspend fun updateView(view: ViewEntity)

    @Delete
    suspend fun deleteView(view: ViewEntity)
}

@Dao
interface AutomationDao {
    @Query("SELECT * FROM automations WHERE libraryId = :libraryId")
    fun getAutomationsByLibrary(libraryId: String): Flow<List<AutomationEntity>>

    @Query("SELECT * FROM automations WHERE libraryId = :libraryId AND isEnabled = 1")
    suspend fun getActiveAutomationsByLibrary(libraryId: String): List<AutomationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomation(automation: AutomationEntity)

    @Update
    suspend fun updateAutomation(automation: AutomationEntity)

    @Delete
    suspend fun deleteAutomation(automation: AutomationEntity)
}

@Dao
interface DashboardWidgetDao {
    @Query("SELECT * FROM dashboard_widgets WHERE workspaceId = :workspaceId ORDER BY `order` ASC")
    fun getWidgetsByWorkspace(workspaceId: String): Flow<List<DashboardWidgetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: DashboardWidgetEntity)

    @Update
    suspend fun updateWidget(widget: DashboardWidgetEntity)

    @Delete
    suspend fun deleteWidget(widget: DashboardWidgetEntity)
}

@Dao
interface AttachmentDao {
    @Query("SELECT * FROM attachments WHERE entryId = :entryId")
    fun getAttachmentsByEntry(entryId: String): Flow<List<AttachmentEntity>>

    @Query("SELECT * FROM attachments WHERE entryId = :entryId")
    suspend fun getAttachmentsByEntryList(entryId: String): List<AttachmentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity)

    @Delete
    suspend fun deleteAttachment(attachment: AttachmentEntity)
}
