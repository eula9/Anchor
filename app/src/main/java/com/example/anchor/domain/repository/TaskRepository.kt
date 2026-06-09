package com.example.anchor.domain.repository

import com.example.anchor.domain.model.Task
import kotlinx.coroutines.flow.Flow

/**
 * 今日任务仓库接口。
 *
 * 负责今日三件事的增删改查，以及跨天自动清空历史任务。
 */
interface TaskRepository {

    /**
     * 观察今日任务列表数据流。
     *
     * 仅返回当天任务，历史日期任务会在订阅时自动清除。
     */
    val todayTasks: Flow<List<Task>>

    /**
     * 添加一条今日任务。
     *
     * @param content 任务内容
     * @return 成功时返回新建任务，失败时返回异常
     */
    suspend fun addTask(content: String): Result<Task>

    /**
     * 切换任务完成状态。
     *
     * @param taskId 任务 ID
     * @param completed 目标完成状态
     */
    suspend fun setTaskCompleted(taskId: Long, completed: Boolean)
}
