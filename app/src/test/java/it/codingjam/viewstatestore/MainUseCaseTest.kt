package it.codingjam.viewstatestore

import assertk.assert
import assertk.assertions.*
import io.mockk.ConstantAnswer
import io.mockk.MockKStubScope
import io.mockk.coEvery
import io.mockk.mockk
import it.codingjam.viewstatestore.lib.Action
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.fold
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import java.io.IOException

private val USER_1 = User(1, "User 1")
private val USER_2 = User(2, "User 2")

class MainUseCaseTest {

    val repository: Repository = mockk()

    private val useCase = MainUseCase(repository)

    @Test
    fun changeStateOnSuccessLoading() {
        coEvery { repository.getList() } returns listOf(USER_1, USER_2)
        coEvery { repository.isStarred(1) } returns true
        coEvery { repository.isStarred(2) } returns false

        val states = runBlocking {
            useCase.getListLce().states(UserListViewState())
        }

        assert(states).hasSize(2)
        assert(states[0].loading).isTrue()
        assert(states[1].error).isNull()
        assert(states[1].loading).isFalse()
        assert(states[1].users).containsExactly(
                USER_1.copy(starred = true),
                USER_2
        )
    }

    @Test
    fun changeStateOnError() {
        coEvery { repository.getList() } returns async { throw IOException() }

        val states = runBlocking {
            useCase.getListLce().states(UserListViewState())
        }

        assert(states).hasSize(2)
        assert(states[0].loading).isTrue()
        assert(states[1].error).isNotNull()
        assert(states[1].loading).isFalse()
        assert(states[1].users).isEmpty()
    }
}

infix fun <T, B> MockKStubScope<Deferred<T>, B>.returns(returnValue: T) = answers(ConstantAnswer(async { returnValue }))

suspend inline fun <T> ReceiveChannel<Action<T>>.states(initialState: T): List<T> {
    return fold(emptyList()) { states, action ->
        states + action(states.lastOrNull() ?: initialState)
    }
}
