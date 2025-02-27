package fr.frankois944.kmpviewmodel.di

import co.touchlab.kermit.Logger
import co.touchlab.kermit.LoggerConfig
import fr.frankois944.kmpviewmodel.logs.buildLoggerConfig
import fr.frankois944.kmpviewmodel.platform.IPlatform
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

/**
 * Koin Module for Logger
 */
@Module
internal class LoggerModule {
    // The Kermit Config need to be created once
    @Single
    fun getLoggerConfig(platform: IPlatform): LoggerConfig =
        buildLoggerConfig(
            isDebug = platform.isDebug,
            isProduction = platform.isProduction,
        )

    // Create an instance of the logger with a optional custom tag
    @Factory
    fun getLoggerWithTag(
        @InjectedParam tag: String?,
        loggerConfig: LoggerConfig,
    ) = Logger(
        config = loggerConfig,
        tag = tag ?: "Shared",
    )
}
