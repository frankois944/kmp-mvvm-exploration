package fr.frankois944.kmpviewmodel.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import fr.frankois944.kmpviewmodel.AppConfig
import fr.frankois944.kmpviewmodel.datastore.DataStoreFileName
import fr.frankois944.kmpviewmodel.datastore.getDataStorePath
import fr.frankois944.kmpviewmodel.platform.IPlatform
import fr.frankois944.kmpviewmodel.platform.getPlatformConfiguration
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("fr.frankois944.kmpviewmodel")
internal class SharedModule {
    @Single
    fun getPlatform(
        @InjectedParam appConfig: AppConfig,
    ): IPlatform = getPlatformConfiguration(appConfig = appConfig)

    @Single
    fun getDataStore(platform: IPlatform): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                getDataStorePath(platform, DataStoreFileName)
            },
        )
    }
}
