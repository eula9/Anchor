package com.example.anchor.data.repository

import com.example.anchor.data.local.dao.DailyRecordDao
import com.example.anchor.data.local.dao.TaskDao
import com.example.anchor.data.local.entity.DailyRecordEntity
import com.example.anchor.data.local.entity.TaskEntity
import com.example.anchor.data.mapper.toDomain
import com.example.anchor.domain.model.Task
import com.example.anchor.domain.model.TaskType
import com.example.anchor.domain.repository.TaskRepository
import com.example.anchor.util.Constants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 任务仓库实现（Room）。
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val dailyRecordDao: DailyRecordDao,
) : TaskRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val todayFixedTasks: Flow<List<Task>> = observeTasksByType(TaskType.FIXED)

    override val todayOptionalTasks: Flow<List<Task>> = observeTasksByType(TaskType.OPTIONAL)

    override suspend fun ensureTodayFixedTasks(templates: List<String>) {
        val today = todayString()
        // 清理非今日任务：可选任务次日消失，固定任务每日重新生成
        taskDao.deleteTasksNotOnDate(today)

        if (templates.isEmpty()) return
        val existingCount = taskDao.countTasksByDateAndType(today, TaskType.FIXED.dbValue)
        if (existingCount > 0) return

        taskDao.insertTasks(
            templates.mapIndexed { index, content ->
                TaskEntity(
                    content = content,
                    completed = false,
                    date = today,
                    type = TaskType.FIXED.dbValue,
                    orderIndex = index,
                )
            },
        )
    }

    override suspend fun addOptionalTask(content: String): Result<Task> {
        val trimmed = content.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(EmptyTaskContentException())
        }

        val today = todayString()
        val count = taskDao.countTasksByDateAndType(today, TaskType.OPTIONAL.dbValue)
        if (count >= Constants.MAX_OPTIONAL_TASKS) {
            return Result.failure(TaskLimitReachedException())
        }

        val newId = taskDao.insertTask(
            TaskEntity(
                content = trimmed,
                completed = false,
                date = today,
                type = TaskType.OPTIONAL.dbValue,
                orderIndex = count,
            ),
        )

        syncTodayRecord()

        return Result.success(
            Task(
                id = newId,
                content = trimmed,
                completed = false,
                date = today,
                type = TaskType.OPTIONAL,
                orderIndex = count,
            ),
        )
    }

    override suspend fun completeTask(taskId: Long) {
        taskDao.completeTask(taskId)
        syncTodayRecord()
    }

    override suspend fun syncTodayRecord() {
        val today = todayString()
        val fixed = taskDao.countTasksByDateAndType(today, TaskType.FIXED.dbValue)
        if (fixed == 0) return

        val tasks = taskDao.getAllTasks().filter { it.date == today }
        val fixedTasks = tasks.filter { it.type == TaskType.FIXED.dbValue }
        val optionalTasks = tasks.filter { it.type == TaskType.OPTIONAL.dbValue }

        val requiredCompleted = fixedTasks.count { it.completed }
        val optionalCompleted = optionalTasks.count { it.completed }
        val allRequiredDone = fixedTasks.isNotEmpty() && fixedTasks.all { it.completed }

        dailyRecordDao.upsertRecord(
            DailyRecordEntity(
                date = today,
                requiredCompleted = requiredCompleted,
                requiredTotal = fixedTasks.size,
                optionalCompleted = optionalCompleted,
                optionalTotal = optionalTasks.size,
                allRequiredDone = allRequiredDone,
            ),
        )
    }

    private fun observeTasksByType(type: TaskType): Flow<List<Task>> = flow {
        emit(todayString())
    }.flatMapLatest { today ->
        taskDao.observeTasksByDateAndType(today, type.dbValue).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    private fun todayString(): String = LocalDate.now().format(dateFormatter)
}

/** 任务内容为空异常 */
class EmptyTaskContentException : Exception("任务内容不能为空")

/** 今日任务数量已达上限异常 */
class TaskLimitReachedException : Exception("今日可选任务已达上限")
