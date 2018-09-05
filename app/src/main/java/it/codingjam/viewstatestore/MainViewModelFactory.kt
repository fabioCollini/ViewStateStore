package it.codingjam.viewstatestore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object MainViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MainViewModel(MainUseCase(Repository())) as T
}