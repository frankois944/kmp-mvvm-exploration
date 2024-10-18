package fr.frankois944.kmpviewmodel.platform

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class Platform(
    override val isDebug: Boolean,
    override val isProduction: Boolean,
) : IPlatform,
    KoinComponent {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
    override val platformContext: NativeContext by inject()
}
