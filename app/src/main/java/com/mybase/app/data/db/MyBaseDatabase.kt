package com.mybase.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mybase.app.data.dao.*
import com.mybase.app.domain.model.*

@Database(
    entities = [
        WorkspaceEntity::class,
        LibraryEntity::class,
        FieldEntity::class,
        EntryEntity::class,
        RelationEntity::class,
        ViewEntity::class,
        AutomationEntity::class,
        DashboardWidgetEntity::class,
        AttachmentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MyBaseDatabase : RoomDatabase() {

    abstract fun workspaceDao(): WorkspaceDao
    abstract fun libraryDao(): LibraryDao
    abstract fun fieldDao(): FieldDao
    abstract fun entryDao(): EntryDao
    abstract fun relationDao(): RelationDao
    abstract fun viewDao(): ViewDao
    abstract fun automationDao(): AutomationDao
    abstract fun dashboardWidgetDao(): DashboardWidgetDao
    abstract fun attachmentDao(): AttachmentDao

    companion object {
        @Volatile
        private var INSTANCE: MyBaseDatabase? = null

        fun getDatabase(context: Context): MyBaseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyBaseDatabase::class.java,
                    "mybase_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
