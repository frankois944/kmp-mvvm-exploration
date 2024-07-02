package fr.frankois944.kmpviewmodel

/**
 * App config
 *
 * @property isDebug the app is build on debug, behavior can change
 * @property isProduction the app is running on production mode (like appstore)
 * @constructor Create empty App config
 */
public data class AppConfig(
    val isDebug: Boolean,
    val isProduction: Boolean,
)
