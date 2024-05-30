package fr.frankois944.kmpviewmodel.platform

import platform.UIKit.UIDevice

internal class Platform(
    override val isDebug: Boolean,
    override val isProduction: Boolean,
    override val platformContext: Any? = null,
) : IPlatform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}
