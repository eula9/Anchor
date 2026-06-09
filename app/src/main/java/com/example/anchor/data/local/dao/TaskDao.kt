package com.example.anchor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.anchor.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * 今日任务 DAO 接口。
 */
@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY id ASC")
    fun observeTasksByDate(date: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT COUNT(*) FROM tasks WHERE date = :date")
    suspend fun countTasksByDate(date: String): Int

    @Insert
    suspend fun insertTask(task: TaskEntity): Long

    @Insert
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Query("UPDATE tasks SET completed = :completed WHERE id = :id")
    suspend fun updateCompleted(id: Long, completed: Boolean)

    @Query("DELETE FROM tasks WHERE date != :today")
    suspend fun deleteTasksNotOnDate(today: String)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    /** 原子替换全部任务（用于数据恢复） */
    @Transaction
    suspend fun replaceAllTasks(tasks: List<TaskEntity>) {
        deleteAllTasks()
        if (tasks.isNotEmpty()) {
            insertTasks(tasks)
        }
    }
}
