package com.example.anchor.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 每日打卡记录实体。
 */
@Entity(tableName = "daily_records")
data class DailyRecordEntity(
    @PrimaryKey
    val date: String,
    val requiredCompleted: Int,
    val requiredTotal: Int,
    val optionalCompleted: Int,
    val optionalTotal: Int,
    val allRequiredDone: Boolean,
)
