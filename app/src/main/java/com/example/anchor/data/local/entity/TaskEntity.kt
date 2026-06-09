package com.example.anchor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 今日任务数据库实体。
 *
 * 对应 Room 表 `tasks`，存储用户每天最多 3 件待办事项。
 *
 * @property id 主键，自增
 * @property content 任务内容
 * @property completed 是否已完成
 * @property date 任务所属日期（yyyy-MM-dd）
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val content: String,
    val completed: Boolean = false,
    val date: String,
)
