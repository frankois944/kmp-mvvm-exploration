package fr.frankois944.kmpviewmodel.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.parameter.parameterSetOf
import org.koin.core.parameter.parametersOf

/**
 * Manage the persisting data, could be improved
 */
@Single
internal class PersistingData(
    storage: DataStore<Preferences>,
) : IPersistingData,
    KoinComponent {
    /**
     * Logger
     */
    private val logger: Logger by inject(parameters = { parameterSetOf("PersistingData") })

    /**
     * Use this value to specify a non null field as a null value
     *
     * The DataStore<Preferences> will not store null values so replace it with *nullValue*
     */
    private val nullValue = "<<NULL>>"

    private companion object {
        const val USER_NAME = "USER_NAME"
        const val IS_ENABLED = "IS_ENABLED"
    }

    //region Username

    private val usernameFactory =
        DataStoreFactory(
            storage = storage,
            key = DataStoreFactory.createKey<String>(USER_NAME),
            logger = get(parameters = { parametersOf("DataStoreFactory[USER_NAME]") }),
        )
    override val usernameFlow: Flow<String?> = usernameFactory.flow
    override var username: String?
        get() = usernameFactory.value.takeIf { it != nullValue }
        set(value) {
            logger.v("UPDATING username $value")
            usernameFactory.value = value ?: nullValue
        }

    //endregion

    //region is Enabled

    private val isEnabledFactory =
        DataStoreFactory(
            storage = storage,
            key = DataStoreFactory.createKey<Boolean>(IS_ENABLED),
            logger = get(parameters = { parametersOf("DataStoreFactory[IS_ENABLED]") }),
        )
    override val isEnabledFlow: Flow<Boolean?> = isEnabledFactory.flow
    override var isEnabled: Boolean
        get() = isEnabledFactory.value ?: false
        set(value) {
            logger.v("UPDATING isEnabled $value")
            isEnabledFactory.value = value
        }

    //endregion
}
