package com.example.anchor.data.mapper

import com.example.anchor.data.local.entity.TaskEntity
import com.example.anchor.domain.model.Task

/**
 * 任务实体与领域模型之间的映射扩展函数。
 */

/** 将数据库实体转换为领域模型 */
fun TaskEntity.toDomain(): Task = Task(
    id = id,
    content = content,
    completed = completed,
    date = date,
)

/** 将领域模型转换为数据库实体 */
fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    content = content,
    completed = completed,
    date = date,
)
