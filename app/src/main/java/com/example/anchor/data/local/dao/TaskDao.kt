package com.example.anchor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.anchor.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * 任务 DAO 接口。
 */
@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE date = :date AND type = :type ORDER BY orderIndex ASC, id ASC")
    fun observeTasksByDateAndType(date: String, type: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY type ASC, orderIndex ASC, id ASC")
    fun observeTasksByDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE date = :date AND type = :type ORDER BY orderIndex ASC, id ASC")
    suspend fun getTasksByDateAndType(date: String, type: Int): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM tasks WHERE date = :date AND type = :type")
    suspend fun countTasksByDateAndType(date: String, type: Int): Int

    @Insert
    suspend fun insertTask(task: TaskEntity): Long

    @Insert
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET completed = :completed WHERE id = :id")
    suspend fun updateCompleted(id: Long, completed: Boolean)

    /** 仅允许从未完成变为完成 */
    @Query("UPDATE tasks SET completed = 1 WHERE id = :id AND completed = 0")
    suspend fun completeTask(id: Long)

    @Query("DELETE FROM tasks WHERE date != :today AND type = 1")
    suspend fun deleteOptionalTasksNotOnDate(today: String)

    @Query("DELETE FROM tasks WHERE date != :today AND type = 0")
    suspend fun deleteFixedTasksNotOnDate(today: String)

    @Query("DELETE FROM tasks WHERE type = 2 AND date < :date")
    suspend fun deleteTomorrowTasksBeforeDate(date: String)

    @Query("DELETE FROM tasks WHERE date = :date AND type = :type")
    suspend fun deleteTasksByDateAndType(date: String, type: Int)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Transaction
    suspend fun replaceAllTasks(tasks: List<TaskEntity>) {
        deleteAllTasks()
        if (tasks.isNotEmpty()) {
            insertTasks(tasks)
        }
    }
}
