package com.example.anchor.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.anchor.domain.repository.IdentityRepository
import com.example.anchor.domain.repository.StreakRepository
import com.example.anchor.domain.repository.TaskRepository

/**
 * HomeViewModel 工厂类。
 */
class HomeViewModelFactory(
    private val identityRepository: IdentityRepository,
    private val taskRepository: TaskRepository,
    private val streakRepository: StreakRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                identityRepository = identityRepository,
                taskRepository = taskRepository,
                streakRepository = streakRepository,
            ) as T
        }
        throw IllegalArgumentException("未知的 ViewModel 类型: ${modelClass.name}")
    }
}
