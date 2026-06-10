package com.example.anchor.domain.repository

import com.example.anchor.domain.model.IdentityAnchor
import kotlinx.coroutines.flow.Flow

/**
 * 身份锚点仓库接口（DataStore）。
 */
interface IdentityRepository {

    /** 是否已完成首次锚点设置 */
    val isSetupComplete: Flow<Boolean>

    /** 当前生效的身份锚点 */
    val activeAnchor: Flow<IdentityAnchor?>

    /** 固定任务模板列表 */
    val fixedTaskTemplates: Flow<List<String>>

    /**
     * 首次设置身份锚点。
     *
     * @param identity 身份宣言
     * @param durationDays 周期天数（7 / 14 / 30）
     * @param fixedTasks 固定任务列表（3~6 条）
     */
    suspend fun setupAnchor(
        identity: String,
        durationDays: Int,
        fixedTasks: List<String>,
    ): Result<Unit>

    /** 延长锚点周期 */
    suspend fun extendAnchor(durationDays: Int): Result<Unit>

    /** 一次性获取当前锚点 */
    suspend fun getActiveAnchor(): IdentityAnchor?
}
