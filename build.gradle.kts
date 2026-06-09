// 项目根级构建脚本：声明所有子模块可用的 Gradle 插件
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
