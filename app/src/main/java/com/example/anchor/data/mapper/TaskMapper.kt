package com.example.anchor.data.mapper

import com.example.anchor.data.local.entity.TaskEntity
import com.example.anchor.domain.model.Task
import com.example.anchor.domain.model.TaskType

/** Entity → Domain */
fun TaskEntity.toDomain(): Task = Task(
    id = id,
    content = content,
    completed = completed,
    date = date,
    type = TaskType.fromDbValue(type),
    orderIndex = orderIndex,
)

/** Domain → Entity */
fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    content = content,
    completed = completed,
    date = date,
    type = type.dbValue,
    orderIndex = orderIndex,
)
