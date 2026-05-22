package com.tuapp.mygame.features.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SetupViewModelFactory(
    private val repository: SetupPreferencesRepo
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SetupViewModel(repository) as T
    }
}