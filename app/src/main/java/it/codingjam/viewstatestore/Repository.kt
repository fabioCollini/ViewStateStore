package it.codingjam.viewstatestore

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.coroutineScope
import kotlinx.coroutines.experimental.delay
import java.util.*

data class User(
        val id: Int,
        val name: String,
        val starred: Boolean = false
)

class Repository {

    suspend fun getList(): Deferred<List<User>> = coroutineScope {
        async {
            val random = Random()
            delay(random.nextInt(10000))
//            if (random.nextBoolean()) {
//                throw IOException()
//            }
            List(random.nextInt(30)) {
                User(it, "User $it")
            }
        }
    }

    suspend fun isStarred(id: Int): Deferred<Boolean> = coroutineScope {
        async {
            val random = Random()
            delay(random.nextInt(50))
            random.nextBoolean()
        }
    }

    suspend fun toggleUser(user: User): Deferred<User> = coroutineScope {
        async {
            val random = Random()
            delay(random.nextInt(2000))
//            if (random.nextBoolean()) {
//                throw IOException()
//            }
            user.copy(starred = !user.starred)
        }
    }
}