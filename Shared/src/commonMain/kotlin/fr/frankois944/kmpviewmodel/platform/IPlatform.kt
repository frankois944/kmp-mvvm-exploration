package fr.frankois944.kmpviewmodel.platform

import fr.frankois944.kmpviewmodel.AppConfig

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public expect class NativeContext

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
     * Platform context for Android target and RootViewController for iOS target
     *
     * Can be casted to an Android Context or an iOS UIViewController
     */
    public val platformContext: NativeContext?
}

/**
 * Get platform
 *
 * @param appConfig
 * @return
 */
public expect fun getPlatformConfiguration(appConfig: AppConfig): IPlatform
