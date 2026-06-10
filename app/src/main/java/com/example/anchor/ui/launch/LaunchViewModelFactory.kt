package com.example.anchor.ui.launch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.anchor.domain.repository.IdentityRepository
import com.example.anchor.domain.repository.MotivationRepository

/**
 * LaunchViewModel 工厂类。
 */
class LaunchViewModelFactory(
    private val identityRepository: IdentityRepository,
    private val motivationRepository: MotivationRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LaunchViewModel::class.java)) {
            return LaunchViewModel(
                identityRepository = identityRepository,
                motivationRepository = motivationRepository,
            ) as T
        }
        throw IllegalArgumentException("未知的 ViewModel 类型: ${modelClass.name}")
    }
}
