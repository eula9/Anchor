package com.example.anchor.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.anchor.R

/**
 * 底部导航栏：首页 / 统计 / 设置。
 */
@Composable
fun AnchorBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
) {
    val items = listOf(
        BottomNavItem(Routes.Home.route, Icons.Filled.Home, R.string.nav_home),
        BottomNavItem(Routes.Stats.route, Icons.AutoMirrored.Filled.List, R.string.nav_stats),
        BottomNavItem(Routes.Settings.route, Icons.Filled.Settings, R.string.nav_settings),
    )

    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.labelRes),
                    )
                },
                label = { Text(text = stringResource(item.labelRes)) },
            )
        }
    }
}

private data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val labelRes: Int,
)
