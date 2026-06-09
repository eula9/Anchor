package com.example.anchor.util

import androidx.work.WorkInfo

/**
 * WorkManager 任务状态格式化工具。
 */
object WorkStatusFormatter {

    /** 将 WorkInfo.State 转为中文描述 */
    fun format(state: WorkInfo.State?): String = when (state) {
        WorkInfo.State.ENQUEUED -> "已排队"
        WorkInfo.State.RUNNING -> "运行中"
        WorkInfo.State.SUCCEEDED -> "已成功"
        WorkInfo.State.FAILED -> "失败"
        WorkInfo.State.BLOCKED -> "阻塞"
        WorkInfo.State.CANCELLED -> "已取消"
        null -> "未调度"
    }
}
