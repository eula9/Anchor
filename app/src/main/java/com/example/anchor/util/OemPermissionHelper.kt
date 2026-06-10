package com.example.anchor.util

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log

/**
 * 国产手机及深度定制 ROM 的后台权限引导。
 *
 * 各厂商不提供自启动状态查询 API，需引导用户手动开启后由用户确认。
 */
object OemPermissionHelper {

    private const val TAG = "OemPermissionHelper"

    private val XIAOMI = setOf("xiaomi", "redmi", "poco", "blackshark")
    private val HUAWEI = setOf("huawei", "honor", "hihonor")
    private val OPPO = setOf("oppo", "oneplus", "realme")
    private val VIVO = setOf("vivo", "iqoo")
    private val MEIZU = setOf("meizu")
    private val OTHER_CN = setOf("lenovo", "zte", "nubia", "coolpad", "tcl", "hisense", "smartisan")

    /** 走标准流程、无需厂商后台引导的品牌 */
    private val STANDARD = setOf("google", "motorola", "nothing", "asus", "sony", "lg", "samsung", "nokia")

    fun detectOemVendor(): OemVendor {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()

        return when {
            manufacturer in XIAOMI || brand in XIAOMI -> OemVendor.XIAOMI
            manufacturer in HUAWEI || brand in HUAWEI -> OemVendor.HUAWEI
            manufacturer in OPPO || brand in OPPO -> OemVendor.OPPO
            manufacturer in VIVO || brand in VIVO -> OemVendor.VIVO
            manufacturer in MEIZU || brand in MEIZU -> OemVendor.MEIZU
            manufacturer in STANDARD || brand in STANDARD -> OemVendor.STANDARD
            manufacturer in OTHER_CN || brand in OTHER_CN -> OemVendor.GENERIC
            // 其余未知厂商按国产通用引导处理（国内机型占比高）
            else -> OemVendor.GENERIC
        }
    }

    fun needsOemBackgroundSetup(isOemBackgroundConfirmed: Boolean): Boolean {
        return detectOemVendor().needsBackgroundSetupGuide && !isOemBackgroundConfirmed
    }

    /**
     * 打开当前厂商的自启动 / 后台相关设置（按 ROM 版本依次尝试）。
     */
    fun createOemBackgroundSettingsIntent(context: Context): Intent {
        val packageName = context.packageName
        val appLabel = context.applicationInfo.loadLabel(context.packageManager).toString()
        val vendor = detectOemVendor()

        val candidates = when (vendor) {
            OemVendor.XIAOMI -> xiaomiIntents(packageName, appLabel)
            OemVendor.HUAWEI -> huaweiIntents(packageName)
            OemVendor.OPPO -> oppoIntents()
            OemVendor.VIVO -> vivoIntents()
            OemVendor.MEIZU -> meizuIntents(packageName)
            OemVendor.GENERIC, OemVendor.STANDARD -> emptyList()
        } + listOf(ReminderPermissionHelper.createAppDetailsIntent(context))

        for (intent in candidates) {
            if (intent.resolveActivity(context.packageManager) != null) {
                return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

        Log.w(TAG, "未找到 ${vendor.displayName} 后台设置页，回退到应用详情")
        return ReminderPermissionHelper.createAppDetailsIntent(context).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    fun createNotificationSettingsIntent(context: Context): Intent {
        return Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun xiaomiIntents(packageName: String, appLabel: String): List<Intent> = listOf(
        Intent().setComponent(
            ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity",
            ),
        ),
        Intent("miui.intent.action.APP_PERM_EDITOR").setComponent(
            ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity",
            ),
        ).apply { putExtra("extra_pkgname", packageName) },
        Intent().setComponent(
            ComponentName(
                "com.miui.powerkeeper",
                "com.miui.powerkeeper.ui.HiddenAppsConfigActivity",
            ),
        ).apply {
            putExtra("package_name", packageName)
            putExtra("package_label", appLabel)
        },
    )

    private fun huaweiIntents(packageName: String): List<Intent> = listOf(
        Intent().setComponent(
            ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
            ),
        ),
        Intent().setComponent(
            ComponentName(
                "com.huawei.systemmanager",
                "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity",
            ),
        ),
        Intent().setComponent(
            ComponentName(
                "com.hihonor.systemmanager",
                "com.hihonor.systemmanager.startupmgr.ui.StartupNormalAppListActivity",
            ),
        ),
        Intent().setComponent(
            ComponentName(
                "com.hihonor.systemmanager",
                "com.hihonor.devicemanager.startupmgr.ui.StartupNormalAppListActivity",
            ),
        ),
    ).map { it.apply { putExtra("packageName", packageName) } }

    private fun oppoIntents(): List<Intent> = listOf(
        Intent().setComponent(
            ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.permission.startup.StartupAppListActivity",
            ),
        ),
        Intent().setComponent(
            ComponentName(
                "com.coloros.safecenter",
                "com.coloros.safecenter.startupapp.StartupAppListActivity",
            ),
        ),
        Intent().setComponent(
            ComponentName(
                "com.oppo.safe",
                "com.oppo.safe.permission.startup.StartupAppListActivity",
            ),
        ),
        Intent().setComponent(
            ComponentName(
                "com.oplus.safecenter",
                "com.oplus.safecenter.startupapp.StartupAppListActivity",
            ),
        ),
    )

    private fun vivoIntents(): List<Intent> = listOf(
        Intent().setComponent(
            ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.BgStartUpManagerActivity",
            ),
        ),
        Intent().setComponent(
            ComponentName(
                "com.iqoo.secure",
                "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity",
            ),
        ),
        Intent().setComponent(
            ComponentName(
                "com.vivo.permissionmanager",
                "com.vivo.permissionmanager.activity.PurviewTabActivity",
            ),
        ),
    )

    private fun meizuIntents(packageName: String): List<Intent> = listOf(
        Intent().setComponent(
            ComponentName(
                "com.meizu.safe",
                "com.meizu.safe.security.SHOW_APPSEC",
            ),
        ).apply {
            putExtra("packageName", packageName)
        },
    )
}
