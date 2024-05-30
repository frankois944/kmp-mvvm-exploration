package fr.frankois944.kmpviewmodel.flows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal fun <T> Flow<T>.asCommonFlow(): CommonFlow<T> = CommonFlow(this)

public class CommonFlow<T>(private val origin: Flow<T>) : Flow<T> by origin {
    public fun watch(block: (T) -> Unit): DisposableClosure {
        val job = Job()

        onEach {
            block(it)
        }.launchIn(CoroutineScope(Dispatchers.Main + job))
        return DisposableClosure { job.cancel() }
    }
}
