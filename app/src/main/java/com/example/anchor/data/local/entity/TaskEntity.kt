package com.example.anchor.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 任务数据库实体。
 *
 * @property type 0 = 固定任务，1 = 可选任务，2 = 明天想做的事
 */
@Entity(
    tableName = "tasks",
    indices = [Index(value = ["date", "type"])],
)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val completed: Boolean = false,
    val date: String,
    val type: Int = 0,
    val orderIndex: Int = 0,
)
