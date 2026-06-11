package com.example.anchor.domain.model

/**
 * 任务类型枚举。
 *
 * - [FIXED] 固定任务：设置身份锚点时定义，每天必须完成
 * - [OPTIONAL] 可选任务：用户每天自由添加
 * - [TOMORROW] 明天想做的事：次日自动转为可选任务
 */
enum class TaskType(val dbValue: Int) {
    FIXED(0),
    OPTIONAL(1),
    TOMORROW(2),
    ;

    companion object {
        fun fromDbValue(value: Int): TaskType =
            entries.firstOrNull { it.dbValue == value } ?: FIXED
    }
}
