package fr.frankois944.kmpviewmodel.flows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal fun <T> StateFlow<T>.asCommonStateFlow(): CommonStateFlow<T> = CommonStateFlow(this)

public class CommonStateFlow<T>(private val origin: StateFlow<T>) : StateFlow<T> by origin {
    public fun watch(block: (T) -> Unit): DisposableClosure {
        val job = Job()

        onEach {
            block(it)
        }.launchIn(CoroutineScope(Dispatchers.Main + job))
        block(value)
        return DisposableClosure { job.cancel() }
    }
}
