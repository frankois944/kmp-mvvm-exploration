package fr.frankois944.kmpviewmodel.models.context

import co.touchlab.kermit.Logger
import fr.frankois944.kmpviewmodel.preferences.IPersistingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parameterSetOf

/**
 * The context contains the data needed by the application to work.
 * It can be accessed from Koin as a singleton or by the static way if no other choice
 */
@Single
public class AppContext(
    private val persistingData: IPersistingData,
) : KoinComponent {
    /**
     * Logger
     */
    private val logger: Logger by inject(parameters = { parameterSetOf("AppContext") })

    public companion object : KoinComponent {
        /**
         * Get directly the appContext singleton instance
         *
         * Useful from NON-JVM env (like iOS)
         */
        public val instance: AppContext by inject()
    }

    //region User id Flow

    /**
     * Private read/write userId Flow
     */
    private val _userIdFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    /**
     * Use this when you want to watch the update of the userId
     */
    public val userIdFlow: Flow<String?> = _userIdFlow

    /**
     * Update the userId and trigger the userIdFlow
     */
    public var userId: String?
        get() = _userIdFlow.value
        set(value) {
            _userIdFlow.value = value
        }
    //endregion

    //region Session token Flow

    /**
     * Private read/write Session Token Flow
     */
    private val _sessionTokenFlow: MutableStateFlow<String?> = MutableStateFlow(null)

    /**
     * Use this when you want to watch the update of the Session Token
     */
    public val sessionTokenFlow: Flow<String?> = _sessionTokenFlow

    /**
     * Update the Session Token and trigger the sessionTokenFlow
     */
    public var sessionToken: String?
        get() = _sessionTokenFlow.value
        set(value) {
            _sessionTokenFlow.value = value
        }
    //endregion

    /**
     * Private read/write Session Token Flow
     */
    public var usernameFlow: Flow<String?> = persistingData.usernameFlow
    public var username: String?
        get() = persistingData.username
        set(value) {
            persistingData.username = value
        }
}
