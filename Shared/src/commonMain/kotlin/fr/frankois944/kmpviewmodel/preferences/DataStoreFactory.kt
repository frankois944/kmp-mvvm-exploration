package fr.frankois944.kmpviewmodel.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.byteArrayPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * A factory for persisting data with KMP DataStore
 */
internal class DataStoreFactory<T>(
    private val storage: DataStore<Preferences>,
    private val key: Preferences.Key<T>,
    private val logger: Logger,
) {
    @Suppress("UNCHECKED_CAST")
    internal companion object {
        private const val PREFERENCE_NAME: String = "CommonPreference"

        // Create a preference key with the given name
        internal inline fun <reified T> createKey(name: String): Preferences.Key<T> =
            when (T::class) {
                String::class -> stringPreferencesKey("$PREFERENCE_NAME.$name") as Preferences.Key<T>
                Int::class -> intPreferencesKey("$PREFERENCE_NAME.$name") as Preferences.Key<T>
                Boolean::class -> booleanPreferencesKey("$PREFERENCE_NAME.$name") as Preferences.Key<T>
                Float::class -> floatPreferencesKey("$PREFERENCE_NAME.$name") as Preferences.Key<T>
                Long::class -> longPreferencesKey("$PREFERENCE_NAME.$name") as Preferences.Key<T>
                Double::class -> doublePreferencesKey("$PREFERENCE_NAME.$name") as Preferences.Key<T>
                ByteArray::class -> byteArrayPreferencesKey("$PREFERENCE_NAME.$name") as Preferences.Key<T>
                else -> throw IllegalArgumentException("Unsupported type: ${T::class}")
            }
    }

    internal var flow: Flow<T?> = storage.data.map { preferences -> preferences[key] }

    internal var value: T?
        get() =
            runBlocking(Dispatchers.Default) {
                logger.v("Getting value - key: $key, value: $value")
                storage.data.map { preferences -> preferences[key] }.first()
            }
        set(value) {
            runBlocking(Dispatchers.Default) {
                storage.edit { preferences ->
                    logger.v("Storing value - key: $key, value: $value")
                    @Suppress("UNCHECKED_CAST")
                    preferences[key] = value as T
                }
            }
        }
}