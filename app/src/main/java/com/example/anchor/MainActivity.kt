package com.example.anchor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.anchor.ui.AnchorApp

/**
 * 应用主 Activity，作为 Compose UI 的宿主。
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as AnchorApplication).appContainer

        setContent {
            // 主题由 AnchorApp 根据用户偏好统一控制
            AnchorApp(appContainer = appContainer)
        }
    }
}
