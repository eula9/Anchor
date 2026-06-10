package com.example.anchor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.anchor.data.local.dao.DailyRecordDao
import com.example.anchor.data.local.dao.TaskDao
import com.example.anchor.data.local.entity.DailyRecordEntity
import com.example.anchor.data.local.entity.TaskEntity

/**
 * Anchor 应用 Room 数据库。
 */
@Database(
    entities = [
        TaskEntity::class,
        DailyRecordEntity::class,
    ],
    version = 3,
    exportSchema = true,
)
abstract class AnchorDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao

    abstract fun dailyRecordDao(): DailyRecordDao

    companion object {
        const val DATABASE_NAME = "anchor_database"
    }
}
