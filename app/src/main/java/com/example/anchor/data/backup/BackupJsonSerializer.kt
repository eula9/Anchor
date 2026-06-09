package com.example.anchor.data.backup

import com.example.anchor.domain.model.BackupData
import com.example.anchor.domain.model.BackupTask
import com.example.anchor.util.Constants
import org.json.JSONArray
import org.json.JSONObject

/**
 * 备份数据 JSON 序列化/反序列化工具。
 */
object BackupJsonSerializer {

    /** 将备份数据序列化为 JSON 字符串 */
    fun toJson(backup: BackupData): String {
        val json = JSONObject().apply {
            put("version", backup.version)
            put("exportTime", backup.exportTime)
            put("identityDate", backup.identityDate)
            put("identityIndex", backup.identityIndex)
            put("notificationEnabled", backup.notificationEnabled)
            put("notificationHour", backup.notificationHour)
            put("notificationMinute", backup.notificationMinute)
            put("themeMode", backup.themeMode)
            put("tasks", JSONArray().apply {
                backup.tasks.forEach { task ->
                    put(JSONObject().apply {
                        put("content", task.content)
                        put("completed", task.completed)
                        put("date", task.date)
                    })
                }
            })
        }
        return json.toString(2)
    }

    /** 从 JSON 字符串解析备份数据 */
    fun fromJson(jsonString: String): BackupData {
        val json = JSONObject(jsonString)
        val version = json.getInt("version")
        require(version <= Constants.BACKUP_VERSION) { "不支持的备份版本" }

        val tasksArray = json.getJSONArray("tasks")
        val tasks = buildList {
            for (i in 0 until tasksArray.length()) {
                val item = tasksArray.getJSONObject(i)
                add(
                    BackupTask(
                        content = item.getString("content"),
                        completed = item.getBoolean("completed"),
                        date = item.getString("date"),
                    ),
                )
            }
        }

        return BackupData(
            version = version,
            exportTime = json.getString("exportTime"),
            identityDate = json.optString("identityDate").ifBlank { null },
            identityIndex = if (json.isNull("identityIndex")) null else json.getInt("identityIndex"),
            notificationEnabled = json.getBoolean("notificationEnabled"),
            notificationHour = json.getInt("notificationHour"),
            notificationMinute = json.getInt("notificationMinute"),
            themeMode = json.getString("themeMode"),
            tasks = tasks,
        )
    }
}
