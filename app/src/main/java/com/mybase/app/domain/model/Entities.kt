package com.mybase.app.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "workspaces")
@Serializable
data class WorkspaceEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String = "",
    val icon: String = "folder",
    val color: String = "#6200EE",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "libraries",
    foreignKeys = [
        ForeignKey(
            entity = WorkspaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["workspaceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workspaceId")]
)
@Serializable
data class LibraryEntity(
    @PrimaryKey
    val id: String,
    val workspaceId: String,
    val name: String,
    val description: String = "",
    val icon: String = "grid_on",
    val color: String = "#03DAC6",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "fields",
    foreignKeys = [
        ForeignKey(
            entity = LibraryEntity::class,
            parentColumns = ["id"],
            childColumns = ["libraryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("libraryId")]
)
@Serializable
data class FieldEntity(
    @PrimaryKey
    val id: String,
    val libraryId: String,
    val name: String,
    val type: FieldType,
    val order: Int = 0,
    val isVisible: Boolean = true,
    val isRequired: Boolean = false,
    val optionsJson: String = "{}",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = LibraryEntity::class,
            parentColumns = ["id"],
            childColumns = ["libraryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("libraryId")]
)
@Serializable
data class EntryEntity(
    @PrimaryKey
    val id: String,
    val libraryId: String,
    val fieldValuesJson: String = "{}",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "relations",
    foreignKeys = [
        ForeignKey(
            entity = LibraryEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceLibraryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = LibraryEntity::class,
            parentColumns = ["id"],
            childColumns = ["targetLibraryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["sourceEntryId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = EntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["targetEntryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("sourceLibraryId"),
        Index("targetLibraryId"),
        Index("sourceEntryId"),
        Index("targetEntryId")
    ]
)
@Serializable
data class RelationEntity(
    @PrimaryKey
    val id: String,
    val fieldId: String,
    val sourceLibraryId: String,
    val targetLibraryId: String,
    val sourceEntryId: String,
    val targetEntryId: String,
    val relationType: RelationType,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "views",
    foreignKeys = [
        ForeignKey(
            entity = LibraryEntity::class,
            parentColumns = ["id"],
            childColumns = ["libraryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("libraryId")]
)
@Serializable
data class ViewEntity(
    @PrimaryKey
    val id: String,
    val libraryId: String,
    val name: String,
    val type: ViewType,
    val configJson: String = "{}",
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "automations",
    foreignKeys = [
        ForeignKey(
            entity = LibraryEntity::class,
            parentColumns = ["id"],
            childColumns = ["libraryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("libraryId")]
)
@Serializable
data class AutomationEntity(
    @PrimaryKey
    val id: String,
    val libraryId: String,
    val name: String,
    val triggerType: TriggerType,
    val triggerFieldId: String? = null,
    val conditionJson: String = "{}",
    val actionType: ActionType,
    val actionConfigJson: String = "{}",
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "dashboard_widgets",
    foreignKeys = [
        ForeignKey(
            entity = WorkspaceEntity::class,
            parentColumns = ["id"],
            childColumns = ["workspaceId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workspaceId")]
)
@Serializable
data class DashboardWidgetEntity(
    @PrimaryKey
    val id: String,
    val workspaceId: String,
    val title: String,
    val type: WidgetType,
    val libraryId: String,
    val configJson: String = "{}",
    val order: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "attachments",
    foreignKeys = [
        ForeignKey(
            entity = EntryEntity::class,
            parentColumns = ["id"],
            childColumns = ["entryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("entryId")]
)
@Serializable
data class AttachmentEntity(
    @PrimaryKey
    val id: String,
    val entryId: String,
    val fieldId: String,
    val fileName: String,
    val filePath: String,
    val mimeType: String,
    val fileSize: Long,
    val createdAt: Long = System.currentTimeMillis()
)
