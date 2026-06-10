package com.example.anchor.domain.model

/**
 * 身份锚点领域模型。
 *
 * @property statement 用户自定义的身份宣言
 * @property startDate 锚点开始日期（yyyy-MM-dd）
 * @property durationDays 锚点周期天数（7 / 14 / 30）
 * @property currentDay 当前处于周期的第几天（从 1 开始）
 * @property daysRemaining 距离周期结束还剩多少天
 * @property isExpired 锚点周期是否已到期
 */
data class IdentityAnchor(
    val statement: String,
    val startDate: String,
    val durationDays: Int,
    val currentDay: Int,
    val daysRemaining: Int,
    val isExpired: Boolean,
)
