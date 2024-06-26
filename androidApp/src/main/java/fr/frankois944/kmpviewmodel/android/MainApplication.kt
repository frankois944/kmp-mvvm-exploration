package fr.frankois944.kmpviewmodel.android

import android.app.Application
import android.os.StrictMode
import fr.frankois944.kmpviewmodel.AppConfig
import fr.frankois944.kmpviewmodel.android.BuildConfig.DEBUG
import fr.frankois944.kmpviewmodel.android.BuildConfig.IS_PRODUCTION
import fr.frankois944.kmpviewmodel.android.di.AndroidModule
import fr.frankois944.kmpviewmodel.startApp
import io.kotzilla.cloudinject.CloudInjectSDK
import io.kotzilla.cloudinject.analytics.koin.analyticsLogger
import org.koin.android.ext.koin.androidContext
import org.koin.ksp.generated.module

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (DEBUG) {
            // See: https://developer.android.com/reference/android/os/StrictMode
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy
                    .Builder()
                    // .detectDiskReads()
                    // .detectDiskWrites()
                    // .detectNetwork() // or .detectAll() for all detectable problems
                    .detectAll()
                    .penaltyLog()
                    .build(),
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy
                    .Builder()
                    // .detectLeakedSqlLiteObjects()
                    // .detectLeakedClosableObjects()
                    .detectAll()
                    .penaltyLog()
                    .build(),
            )
        }
        CloudInjectSDK.setup(this)
        startApp(
            appConfig =
                AppConfig(
                    isDebug = DEBUG,
                    isProduction = IS_PRODUCTION,
                ),
        ) {
            analyticsLogger()
            androidContext(this@MainApplication)
            modules(AndroidModule().module)
        }
    }
}
