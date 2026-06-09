package com.example.anchor.ui.navigation

/**
 * 应用导航路由定义。
 */
sealed class Routes(val route: String) {

    /** 首页路由 */
    data object Home : Routes("home")

    /** 设置页路由 */
    data object Settings : Routes("settings")
}
