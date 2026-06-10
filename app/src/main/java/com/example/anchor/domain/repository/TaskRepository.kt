package com.example.anchor.domain.repository

import com.example.anchor.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * 任务仓库接口（Room）。
 */
interface TaskRepository {

    /** 观察今日固定任务 */
    val todayFixedTasks: Flow<List<Task>>

    /** 观察今日可选任务 */
    val todayOptionalTasks: Flow<List<Task>>

    /** 确保今日固定任务已从模板生成 */
    suspend fun ensureTodayFixedTasks(templates: List<String>)

    /** 添加可选任务 */
    suspend fun addOptionalTask(content: String): Result<Task>

    /** 标记任务为完成（不可撤销） */
    suspend fun completeTask(taskId: Long)

    /** 同步今日打卡记录（用于连续天数统计） */
    suspend fun syncTodayRecord()
}
