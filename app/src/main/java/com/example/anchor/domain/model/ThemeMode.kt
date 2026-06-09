package com.example.anchor.domain.model

/**
 * 应用主题模式。
 */
enum class ThemeMode {
    /** 跟随系统设置 */
    SYSTEM,

    /** 浅色模式 */
    LIGHT,

    /** 深色模式 */
    DARK;

    companion object {
        /** 从持久化字符串解析主题模式，无法识别时返回 SYSTEM */
        fun fromString(value: String?): ThemeMode = when (value) {
            LIGHT.name -> LIGHT
            DARK.name -> DARK
            else -> SYSTEM
        }
    }
}
