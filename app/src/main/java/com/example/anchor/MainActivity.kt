package com.example.anchor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.anchor.ui.AnchorApp
import kotlinx.coroutines.launch

/**
 * 应用主 Activity，作为 Compose UI 的宿主。
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as AnchorApplication).appContainer

        setContent {
            AnchorApp(appContainer = appContainer)
        }
    }

    override fun onResume() {
        super.onResume()
        val container = (application as AnchorApplication).appContainer
        lifecycleScope.launch {
            container.notificationRepository.ensureReminderScheduled()
        }
    }
}
