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

    fun toJson(backup: BackupData): String {
        val json = JSONObject().apply {
            put("version", backup.version)
            put("exportTime", backup.exportTime)
            put("isSetupComplete", backup.isSetupComplete)
            put("identity", backup.identity)
            put("startDate", backup.startDate)
            put("durationDays", backup.durationDays)
            put("fixedTaskTemplates", JSONArray(backup.fixedTaskTemplates))
            put("notificationEnabled", backup.notificationEnabled)
            put("notificationHour", backup.notificationHour)
            put("notificationMinute", backup.notificationMinute)
            put("themeMode", backup.themeMode)
            put("currentStreak", backup.currentStreak)
            put("longestStreak", backup.longestStreak)
            put("lastPerfectDate", backup.lastPerfectDate)
            put("tasks", JSONArray().apply {
                backup.tasks.forEach { task ->
                    put(JSONObject().apply {
                        put("content", task.content)
                        put("completed", task.completed)
                        put("date", task.date)
                        put("type", task.type)
                        put("orderIndex", task.orderIndex)
                    })
                }
            })
        }
        return json.toString(2)
    }

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
                        type = item.optInt("type", 0),
                        orderIndex = item.optInt("orderIndex", 0),
                    ),
                )
            }
        }

        val templatesArray = json.optJSONArray("fixedTaskTemplates")
        val templates = buildList {
            if (templatesArray != null) {
                for (i in 0 until templatesArray.length()) {
                    add(templatesArray.getString(i))
                }
            }
        }

        return BackupData(
            version = version,
            exportTime = json.getString("exportTime"),
            isSetupComplete = json.optBoolean("isSetupComplete", false),
            identity = json.optString("identity").ifBlank { null },
            startDate = json.optString("startDate").ifBlank { null },
            durationDays = if (json.has("durationDays") && !json.isNull("durationDays")) {
                json.getInt("durationDays")
            } else {
                null
            },
            fixedTaskTemplates = templates,
            notificationEnabled = json.getBoolean("notificationEnabled"),
            notificationHour = json.getInt("notificationHour"),
            notificationMinute = json.getInt("notificationMinute"),
            themeMode = json.getString("themeMode"),
            currentStreak = json.optInt("currentStreak", 0),
            longestStreak = json.optInt("longestStreak", 0),
            lastPerfectDate = json.optString("lastPerfectDate").ifBlank { null },
            tasks = tasks,
        )
    }
}
