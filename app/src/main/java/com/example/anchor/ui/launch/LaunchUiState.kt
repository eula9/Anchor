package com.example.anchor.ui.launch

import com.example.anchor.domain.model.IdentityAnchor

/**
 * 启动页 UI 状态。
 */
data class LaunchUiState(
    val isLoading: Boolean = true,
    val activeAnchor: IdentityAnchor? = null,
    val motivationQuote: String = "",
)
