package com.example.anchor.data.mapper

import com.example.anchor.data.local.entity.DailyRecordEntity
import com.example.anchor.domain.model.DailyRecord

/** Entity → Domain */
fun DailyRecordEntity.toDomain(): DailyRecord = DailyRecord(
    date = date,
    requiredCompleted = requiredCompleted,
    requiredTotal = requiredTotal,
    optionalCompleted = optionalCompleted,
    optionalTotal = optionalTotal,
    allRequiredDone = allRequiredDone,
)

/** Domain → Entity */
fun DailyRecord.toEntity(): DailyRecordEntity = DailyRecordEntity(
    date = date,
    requiredCompleted = requiredCompleted,
    requiredTotal = requiredTotal,
    optionalCompleted = optionalCompleted,
    optionalTotal = optionalTotal,
    allRequiredDone = allRequiredDone,
)
