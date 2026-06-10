package com.example.anchor.domain.model

/**
 * 每日打卡记录领域模型。
 *
 * @property date 日期（yyyy-MM-dd）
 * @property requiredCompleted 固定任务已完成数
 * @property requiredTotal 固定任务总数
 * @property optionalCompleted 可选任务已完成数
 * @property optionalTotal 可选任务总数
 * @property allRequiredDone 是否完成全部固定任务
 */
data class DailyRecord(
    val date: String,
    val requiredCompleted: Int,
    val requiredTotal: Int,
    val optionalCompleted: Int,
    val optionalTotal: Int,
    val allRequiredDone: Boolean,
)
