package com.example.anchor.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.anchor.domain.repository.IdentityRepository
import com.example.anchor.domain.repository.TaskRepository

/**
 * SetupViewModel 工厂类。
 */
class SetupViewModelFactory(
    private val identityRepository: IdentityRepository,
    private val taskRepository: TaskRepository,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SetupViewModel::class.java)) {
            return SetupViewModel(
                identityRepository = identityRepository,
                taskRepository = taskRepository,
            ) as T
        }
        throw IllegalArgumentException("未知的 ViewModel 类型: ${modelClass.name}")
    }
}
