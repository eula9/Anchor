package com.example.anchor.data.repository

import com.example.anchor.data.local.dao.DailyRecordDao
import com.example.anchor.data.local.entity.DailyRecordEntity
import com.example.anchor.domain.model.StatisticsInfo
import com.example.anchor.domain.repository.StatisticsRepository
import com.example.anchor.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 统计数据仓库实现。
 */
class StatisticsRepositoryImpl(
    private val dailyRecordDao: DailyRecordDao,
    private val streakRepository: StreakRepository,
) : StatisticsRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override val statistics: Flow<StatisticsInfo> = combine(
        dailyRecordDao.observeAllRecords(),
        streakRepository.streakInfo,
    ) { records, streak ->
        buildStatistics(records, streak.currentStreak, streak.longestStreak)
    }

    private fun buildStatistics(
        records: List<DailyRecordEntity>,
        currentStreak: Int,
        longestStreak: Int,
    ): StatisticsInfo {
        val totalFixed = records.sumOf { it.requiredCompleted }
        val totalOptional = records.sumOf { it.optionalCompleted }
        val perfectDays = records.count { it.allRequiredDone }
        val actionRate = if (records.isEmpty()) {
            0
        } else {
            (perfectDays * 100) / records.size
        }

        val today = LocalDate.now()
        val weeklyLabels = (6 downTo 0).map { offset ->
            today.minusDays(offset.toLong()).format(DateTimeFormatter.ofPattern("M/d"))
        }
        val weeklyFixed = (6 downTo 0).map { offset ->
            val date = today.minusDays(offset.toLong()).format(dateFormatter)
            records.firstOrNull { it.date == date }?.requiredCompleted ?: 0
        }
        val weeklyOptional = (6 downTo 0).map { offset ->
            val date = today.minusDays(offset.toLong()).format(dateFormatter)
            records.firstOrNull { it.date == date }?.optionalCompleted ?: 0
        }

        return StatisticsInfo(
            actionRate = actionRate,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalFixedCompleted = totalFixed,
            totalOptionalCompleted = totalOptional,
            weeklyFixedCounts = weeklyFixed,
            weeklyOptionalCounts = weeklyOptional,
            weeklyLabels = weeklyLabels,
        )
    }
}
