package fr.frankois944.kmpviewmodel.logs

import co.touchlab.kermit.Severity
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter

/**
 * Setup the logger and the log writers + severity, used by : LoggerModule.kt
 *
 * @param isDebug The app on debug mode which change the behaviour of the logger
 * @param isProduction The app on production mode which change the behaviour of the logger
 *
 */
internal fun buildLoggerConfig(
    isDebug: Boolean,
    isProduction: Boolean,
) = if (isProduction) {
    loggerConfigInit(
        logWriters = arrayOf(),
        minSeverity = Severity.Error,
    )
} else {
    loggerConfigInit(
        logWriters = arrayOf(platformLogWriter()),
        minSeverity = Severity.Verbose,
    )
}

// public fun log(tag: String?): Logger = getKoin().get<Logger>(parameters = { parametersOf(tag) })
