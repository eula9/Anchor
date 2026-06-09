package com.example.anchor.domain.repository

import android.net.Uri

/**
 * 数据备份仓库接口。
 */
interface BackupRepository {

    /** 导出备份到指定 URI */
    suspend fun exportBackup(uri: Uri): Result<Unit>

    /** 从指定 URI 导入备份 */
    suspend fun importBackup(uri: Uri): Result<Unit>
}
