package it.codingjam.viewstatestore

import androidx.lifecycle.ViewModel
import it.codingjam.viewstatestore.lib.ViewStateStore

data class UserListViewState(
        val users: List<User> = emptyList(),
        val loading: Boolean = false,
        val error: Throwable? = null
)

class MainViewModel(
        private val useCase: MainUseCase
) : ViewModel() {

    val store = ViewStateStore(UserListViewState())

    init {
        loadData()
    }

    fun loadData() {
        store.dispatchActions(useCase.getListLce())
//        store.dispatchAction { useCase.getList() }
    }

    fun toggleUser(position: Int) {
        val state = store.state()
        val newState = state.copy(users = state.users.replaceAt(position) {
            useCase.toggleUser(it)
        })
        store.dispatchState(newState)
    }

    fun toggleUserAsync(position: Int) {
        store.dispatchAction {
            useCase.toggleUser(store.state(), position)
        }
    }

    override fun onCleared() {
        store.cancel()
    }
}

inline fun <T> List<T>.replaceAt(position: Int, f: (T) -> T): List<T> {
    return mapIndexed { index, item ->
        if (index == position) f(item) else item
    }
}
