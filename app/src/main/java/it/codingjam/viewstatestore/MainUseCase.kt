package it.codingjam.viewstatestore

import it.codingjam.viewstatestore.lib.Action
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.channels.ProducerScope
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.coroutineScope

class MainUseCase(private val repository: Repository) {

    suspend fun getList(): Action<UserListViewState> {
        val users = repository.getList().await()
                .map { it to repository.isStarred(it.id) }
                .map { (user, deferred) ->
                    user.copy(starred = deferred.await())
                }
        return Action { UserListViewState(users) }
    }

    fun getListLce(): ReceiveChannel<Action<UserListViewState>> = produceActions {
        send { copy(loading = true, error = null) }
        try {
            val users = repository.getList().await()
                    .map { it to repository.isStarred(it.id) }
                    .map { (user, deferred) ->
                        user.copy(starred = deferred.await())
                    }
            send { copy(users = users, loading = false) }
        } catch (e: Exception) {
            send { copy(error = e, loading = false) }
        }
    }

    suspend fun getListLce2(): ReceiveChannel<Action<UserListViewState>> = coroutineScope {
        produce<Action<UserListViewState>> {
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
    }

    fun toggleUser(user: User): User = user.copy(starred = !user.starred)

    fun toggleUserSync(state: UserListViewState, pos: Int): UserListViewState {
        val currentUser = state.users[pos]
        val newUser = currentUser.copy(starred = !currentUser.starred)
        return state.copy(users = state.users.replaceAt(pos, newUser))
    }

    suspend fun toggleUserAsync(user: User): User = repository.toggleUser(user).await()

    suspend fun toggleUser(state: UserListViewState, position: Int): Action<UserListViewState> {
        val newUser = repository.toggleUser(state.users[position]).await()
        return Action {
            copy(users = users.replaceAt(position) { newUser })
        }
    }
}

fun <T> produceActions(f: suspend ProducerScope<Action<T>>.() -> Unit): ReceiveChannel<Action<T>> =
        GlobalScope.produce(block = f)

suspend fun <T> ProducerScope<Action<T>>.send(f: T.() -> T) = send(Action(f))