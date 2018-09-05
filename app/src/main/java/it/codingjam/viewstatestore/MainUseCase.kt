package it.codingjam.viewstatestore

import it.codingjam.viewstatestore.lib.Action
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce

class MainUseCase(private val repository: Repository) {

    suspend fun getList(): Action<UserListViewState> {
        val users = repository.getList().await()
                .map { it to repository.isStarred(it.id) }
                .map { (user, deferred) ->
                    user.copy(starred = deferred.await())
                }
        return Action { UserListViewState(users) }
    }

    fun getListLce(): ReceiveChannel<Action<UserListViewState>> = produce {
        send(Action { copy(loading = true, error = null) })
        try {
            val users = repository.getList().await()
                    .map { it to repository.isStarred(it.id) }
                    .map { (user, deferred) ->
                        user.copy(starred = deferred.await())
                    }
            send(Action { copy(users = users, loading = false) })
        } catch (e: Exception) {
            send(Action { copy(error = e, loading = false) })
        }
    }

    fun toggleUser(user: User): User = user.copy(starred = !user.starred)

    suspend fun toggleUserAsync(user: User): User = repository.toggleUser(user).await()

    suspend fun toggleUser(state: UserListViewState, position: Int): Action<UserListViewState> {
        val newUser = toggleUserAsync(state.users[position])
        return Action {
            copy(users = users.replaceAt(position) { newUser })
        }
    }
}