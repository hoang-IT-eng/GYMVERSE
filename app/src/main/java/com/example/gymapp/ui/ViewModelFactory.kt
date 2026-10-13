package com.example.gymapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.gymapp.GymApp

inline fun <reified T : ViewModel> appViewModelFactory(
    crossinline create: (GymApp) -> T,
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    override fun <VM : ViewModel> create(modelClass: Class<VM>, extras: CreationExtras): VM {
        val app = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as GymApp
        @Suppress("UNCHECKED_CAST")
        return create(app) as VM
    }
}
