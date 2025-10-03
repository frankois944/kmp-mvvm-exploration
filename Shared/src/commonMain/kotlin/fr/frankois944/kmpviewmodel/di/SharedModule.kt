package fr.frankois944.kmpviewmodel.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import fr.frankois944.kmpviewmodel.AppConfig
import fr.frankois944.kmpviewmodel.platform.IPlatform
import fr.frankois944.kmpviewmodel.platform.getPlatformConfiguration
import fr.frankois944.kmpviewmodel.preferences.DataStoreFileName
import fr.frankois944.kmpviewmodel.preferences.getDataStorePath
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/**
 * Module With custom Implementation
 */
@Module
@Configuration
@ComponentScan("fr.frankois944.kmpviewmodel")
internal class SharedModule {
    @Single
    fun getPlatform(
        @InjectedParam appConfig: AppConfig,
    ): IPlatform = getPlatformConfiguration(appConfig = appConfig)

    @Single
    fun getDataStore(platform: IPlatform): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                getDataStorePath(platform, DataStoreFileName)
            },
        )
}
