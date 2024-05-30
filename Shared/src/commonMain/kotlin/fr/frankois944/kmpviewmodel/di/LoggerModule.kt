package fr.frankois944.kmpviewmodel.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.LoggerConfig
import fr.frankois944.kmpviewmodel.logs.buildLoggerConfig
import fr.frankois944.kmpviewmodel.platform.IPlatform
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
internal class LoggerModule {
    @Single
    fun getLoggerConfig(platform: IPlatform): LoggerConfig =
        buildLoggerConfig(
            isDebug = platform.isDebug,
            isProduction = platform.isProduction,
        )

    @Factory
    fun getLoggerWithTag(
        @InjectedParam tag: String?,
        loggerConfig: LoggerConfig,
    ) = Logger(
        config = loggerConfig,
        tag = tag ?: "Shared",
    )
}
