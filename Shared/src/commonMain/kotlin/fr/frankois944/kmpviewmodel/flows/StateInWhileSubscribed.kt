package fr.frankois944.kmpviewmodel.flows

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

internal expect val timeout: Long

/**
 * State in while subscribed with timeout specified by the targeted platform
 */
internal fun <T> Flow<T>.stateInWhileSubscribed(
    viewModelScope: CoroutineScope,
    initialValue: T,
) = stateIn(
    scope = viewModelScope,
    // Why using a timeout? on android it's 5 seconds and on iOS don't need it
    // https://proandroiddev.com/loading-initial-data-in-launchedeffect-vs-viewmodel-f1747c20ce62
    started = SharingStarted.WhileSubscribed(timeout),
    initialValue = initialValue,
)
