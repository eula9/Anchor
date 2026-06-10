package com.example.anchor.util

import com.example.anchor.R

/**
 * 国产及深度定制 Android 厂商分类。
 */
enum class OemVendor(val displayName: String) {
    XIAOMI("小米/红米"),
    HUAWEI("华为/荣耀"),
    OPPO("OPPO/一加/realme"),
    VIVO("vivo/iQOO"),
    MEIZU("魅族"),
    GENERIC("国产手机"),
    STANDARD("标准系统"),
    ;

    /** 是否需要额外的自启动 / 后台保活设置引导 */
    val needsBackgroundSetupGuide: Boolean
        get() = this != STANDARD

    fun guideStringRes(): Int = when (this) {
        XIAOMI -> R.string.notification_oem_guide_xiaomi
        HUAWEI -> R.string.notification_oem_guide_huawei
        OPPO -> R.string.notification_oem_guide_oppo
        VIVO -> R.string.notification_oem_guide_vivo
        MEIZU -> R.string.notification_oem_guide_meizu
        GENERIC -> R.string.notification_oem_guide_generic
        STANDARD -> R.string.notification_oem_guide_generic
    }
}
