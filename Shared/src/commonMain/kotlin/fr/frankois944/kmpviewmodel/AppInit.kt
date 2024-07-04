package fr.frankois944.kmpviewmodel

import co.touchlab.kermit.Logger
import co.touchlab.kermit.koin.KermitKoinLogger
import fr.frankois944.kmpviewmodel.di.LoggerModule
import fr.frankois944.kmpviewmodel.di.SharedModule
import fr.frankois944.kmpviewmodel.logs.buildLoggerConfig
import fr.frankois944.kmpviewmodel.platform.IPlatform
import io.kotzilla.cloudinject.CloudInjectCoreSDK
import io.kotzilla.cloudinject.analytics.koin.analyticsLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.ksp.generated.module

/**
 * Init the application and all dependencies, it's the entry point
 *
 * @param appConfig An application configuration only retrievable from the app
 * @param nativeAppDeclaration Load some koin declaration, only used by Android
 * @return A koin context
 */
public fun startApp(
    appConfig: AppConfig,
    nativeAppDeclaration: KoinAppDeclaration? = null,
): KoinApplication {
    CloudInjectCoreSDK.setupAndConnect("fr.frankois944.kmpviewmodel.ios", "1.0")
    // Initialize Koin in sync way
    return startKoin {
        if (!appConfig.isProduction) { // on production, do not print logs
            // use Koin logger with Kermit
            KermitKoinLogger(
                logger =
                    Logger(
                        config =
                            buildLoggerConfig(
                                isDebug = appConfig.isDebug,
                                isProduction = appConfig.isProduction,
                            ),
                        tag = "koin",
                    ),
            )
            // enable log printer
            printLogger()
        }
        // load common declaration
        modules(
            SharedModule().module,
            LoggerModule().module,
        )
        // load native koin declaration (Android)
        nativeAppDeclaration?.let { it() }
        analyticsLogger()
    }.also {
        // We can complete the koin initialisation here, like async load modules...
        // inject AppConfig parameter in IPlatform interface. See SharedModule.kt
        it.koin.get<IPlatform> { parametersOf(appConfig) }
    }
}
