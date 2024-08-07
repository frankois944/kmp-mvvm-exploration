package fr.frankois944.kmpviewmodel.platform

import fr.frankois944.kmpviewmodel.AppConfig

public actual fun getPlatformConfiguration(appConfig: AppConfig): IPlatform =
    Platform(
        isDebug = appConfig.isDebug,
        isProduction = appConfig.isProduction,
    )
