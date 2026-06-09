package com.example.anchor.data.repository

import com.example.anchor.data.local.dao.TaskDao
import com.example.anchor.data.local.entity.TaskEntity
import com.example.anchor.data.mapper.toDomain
import com.example.anchor.domain.model.Task
import com.example.anchor.domain.repository.TaskRepository
import com.example.anchor.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 今日任务仓库实现类。
 *
 * 核心逻辑：
 * 1. 每天最多创建 3 条任务
 * 2. 支持勾选完成状态
 * 3. 第二天自动删除非今日的历史任务
 */
class TaskRepositoryImpl(
    private val taskDao: TaskDao,
) : TaskRepository {

    /** ISO 本地日期格式化器（yyyy-MM-dd） */
    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val todayTasks: Flow<List<Task>> = flow {
        // 每次订阅时先清理历史任务，确保第二天自动清空
        clearStaleTasks()
        emit(getTodayDate())
    }.flatMapLatest { today ->
        taskDao.observeTasksByDate(today).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addTask(content: String): Result<Task> {
        val trimmedContent = content.trim()
        if (trimmedContent.isEmpty()) {
            return Result.failure(EmptyTaskContentException())
        }

        val today = getTodayDate()
        clearStaleTasks()

        val currentCount = taskDao.countTasksByDate(today)
        if (currentCount >= Constants.MAX_DAILY_TASKS) {
            return Result.failure(TaskLimitReachedException())
        }

        val newId = taskDao.insertTask(
            TaskEntity(
                content = trimmedContent,
                completed = false,
                date = today,
            ),
        )

        return Result.success(
            Task(
                id = newId,
                content = trimmedContent,
                completed = false,
                date = today,
            ),
        )
    }

    override suspend fun setTaskCompleted(taskId: Long, completed: Boolean) {
        taskDao.updateCompleted(taskId, completed)
    }

    /** 获取今日日期字符串 */
    private fun getTodayDate(): String = LocalDate.now().format(dateFormatter)

    /** 删除非今日的历史任务 */
    private suspend fun clearStaleTasks() {
        taskDao.deleteTasksNotOnDate(getTodayDate())
    }
}

/** 任务内容为空异常 */
class EmptyTaskContentException : Exception("任务内容不能为空")

/** 今日任务数量已达上限异常 */
class TaskLimitReachedException : Exception("今日任务已达上限")
