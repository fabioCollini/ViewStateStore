package it.codingjam.viewstatestore.lib

import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.consumeEach
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

class Action<T>(private val f: T.() -> T) {
    operator fun invoke(t: T) = t.f()
}

class ViewStateStore<T : Any>(
        initialState: T
) {

    private val liveData = MutableLiveData<T>().apply {
        value = initialState
    }

    private val job = Job()

    fun observe(owner: LifecycleOwner, observer: (T) -> Unit) =
            liveData.observe(owner, Observer { observer(it!!) })

    @MainThread
    fun dispatchState(state: T) {
        liveData.value = state
    }

    @MainThread
    fun dispatchAction(f: suspend () -> Action<T>) {
        launch(CommonPool + job) {
            val action = f()
            withContext(UI) {
                dispatchState(action(state()))
            }
        }
    }

    fun dispatchAction2(f: suspend () -> ((T) -> T)) {
        launch(CommonPool + job) {
            val action = f()
            withContext(UI) {
                dispatchState(action(state()))
            }
        }
    }

    fun dispatchActions(f: ReceiveChannel<Action<T>>) {
        launch(CommonPool + job) {
            f.consumeEach { action ->
                withContext(UI) {
                    dispatchState(action(state()))
                }
            }
        }
    }

    fun state() = liveData.value!!

    fun cancel() = job.cancel()
}