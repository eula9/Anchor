package com.example.anchor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.anchor.data.local.dao.TaskDao
import com.example.anchor.data.local.entity.TaskEntity

/**
 * Anchor 应用 Room 数据库定义。
 *
 * 作为本地持久化层的核心入口，注册所有 Entity 与 Dao。
 * 数据库版本升级时在此调整 version 并编写 Migration。
 */
@Database(
    entities = [
        TaskEntity::class,
    ],
    version = 2,
    exportSchema = true,
)
abstract class AnchorDatabase : RoomDatabase() {

    /** 今日任务 DAO */
    abstract fun taskDao(): TaskDao

    companion object {
        /** 数据库文件名 */
        const val DATABASE_NAME = "anchor_database"
    }
}
