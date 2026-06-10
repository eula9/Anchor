package com.example.anchor.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.anchor.data.local.entity.DailyRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * 每日打卡记录 DAO。
 */
@Dao
interface DailyRecordDao {

    @Query("SELECT * FROM daily_records WHERE date = :date LIMIT 1")
    suspend fun getRecordByDate(date: String): DailyRecordEntity?

    @Query("SELECT * FROM daily_records ORDER BY date DESC")
    fun observeAllRecords(): Flow<List<DailyRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertRecord(record: DailyRecordEntity)

    @Query("DELETE FROM daily_records")
    suspend fun deleteAllRecords()
}
