@file:OptIn(ExperimentalCoroutinesApi::class)

package fr.frankois944.kmpviewmodel.viewmodels.mainscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import fr.frankois944.kmpviewmodel.helpers.eventbus.AppEvents
import fr.frankois944.kmpviewmodel.helpers.eventbus.IEventBus
import fr.frankois944.kmpviewmodel.logs.log
import fr.frankois944.kmpviewmodel.models.context.AppContext
import fr.frankois944.kmpviewmodel.models.services.account.IAccountService
import fr.frankois944.kmpviewmodel.models.services.profile.IProfileService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import org.koin.core.annotation.InjectedParam
import org.koin.core.component.KoinComponent
import kotlin.random.Random

@KoinViewModel
public class MainScreenViewModel(
    @InjectedParam
    public val param1: String?,
    private val profileService: IProfileService,
    private val accountService: IAccountService,
    private val appContext: AppContext,
    private val eventBus: IEventBus,
    private val logger: Logger = log("MainScreenViewModel"),
) : ViewModel(),
    KoinComponent {
    init {
        logger.d("INIT with params $param1")
    }

    // <editor-fold desc="MainScreenUIState">

    private val _mainScreenUIState = MutableSharedFlow<MainScreenUIState>()
    public val mainScreenUIState: StateFlow<MainScreenUIState> =
        listOf(_mainScreenUIState, loadContent())
            .merge()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), MainScreenUIState.Loading)

    private fun loadContent(reloading: Boolean = false): Flow<MainScreenUIState> =
        flow {
            try {
                if (!reloading && mainScreenUIState.value is MainScreenUIState.Success) {
                    return@flow
                }
                emit(MainScreenUIState.Loading)
                logger.d("START LOADING SCREEN")
                val profileData = profileService.getProfile()
                val accountData = accountService.getAccountInfo()
                delay(3000)
                // simulate an error
                if (reloading) {
                    throw Exception("Oups")
                }
                logger.d("OK LOADING SCREEN")
                emit(MainScreenUIState.Success(profileData, accountData))
            } catch (ex: Exception) {
                logger.e("FAILING LOADING SCREEN", ex)
                emit(MainScreenUIState.Error("Something bad happened $ex"))
            }
        }

    public fun reload(): Job =
        viewModelScope.launch {
            _mainScreenUIState.emitAll(loadContent(true))
        }
    // </editor-fold>

    // <editor-fold desc="UserId">
    public val userId: StateFlow<String?> =
        appContext.userIdFlow
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    public fun updateUserId() {
        logger.d("updateUserId")
        appContext.userId = Random.nextInt().toString()
        eventBus.publish(AppEvents.SHARE_CONTENT)
    }
    // </editor-fold>

    // <editor-fold desc="CleanUP">
    override fun onCleared() {
        super.onCleared()
        logger.d("onCleared")
    }
    // </editor-fold>
}
