package com.example.anchor.domain.model

/**
 * 今日任务领域模型。
 *
 * @property id 任务唯一标识
 * @property content 任务内容
 * @property completed 是否已完成
 * @property date 任务所属日期（yyyy-MM-dd）
 */
data class Task(
    val id: Long,
    val content: String,
    val completed: Boolean,
    val date: String,
)
