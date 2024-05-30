package fr.frankois944.kmpviewmodel.platform

import fr.frankois944.kmpviewmodel.AppConfig

/**
 * Platform
 *
 * @constructor Create empty Platform
 */
public interface IPlatform {
    /**
     * Name
     */
    public val name: String

    /**
     * Is debug
     */
    public val isDebug: Boolean

    /**
     * Is production
     */
    public val isProduction: Boolean

    /**
     * Platform context for Android target ONLY
     *
     * Can be casted to an Android Context
     */
    public val platformContext: Any?
}

/**
 * Get platform
 *
 * @param appConfig
 * @return
 */
public expect fun getPlatformConfiguration(appConfig: AppConfig): IPlatform
