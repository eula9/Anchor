package com.example.anchor.ui.navigation

/**
 * 应用导航路由定义。
 */
sealed class Routes(val route: String) {

    /** 首次设置页 */
    data object Setup : Routes("setup")

    /** 启动页（身份宣言 + 今日一句） */
    data object Launch : Routes("launch")

    /** 首页 */
    data object Home : Routes("home")

    /** 统计页 */
    data object Stats : Routes("stats")

    /** 设置页 */
    data object Settings : Routes("settings")
}
