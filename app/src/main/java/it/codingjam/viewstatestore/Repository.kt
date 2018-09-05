package it.codingjam.viewstatestore

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import java.util.*

data class User(
        val id: Int,
        val name: String,
        val starred: Boolean = false
)

class Repository {

    fun getList(): Deferred<List<User>> {
        return async {
            val random = Random()
            delay(random.nextInt(2000))
//            if (random.nextBoolean()) {
//                throw IOException()
//            }
            List(random.nextInt(30)) {
                User(it,"User $it")
            }
        }
    }

    fun isStarred(id: Int): Deferred<Boolean> {
        return async {
            val random = Random()
            delay(random.nextInt(50))
            random.nextBoolean()
        }
    }

    fun toggleUser(user: User): Deferred<User> {
        return async {
            val random = Random()
            delay(random.nextInt(2000))
//            if (random.nextBoolean()) {
//                throw IOException()
//            }
            user.copy(starred = !user.starred)
        }
    }
}