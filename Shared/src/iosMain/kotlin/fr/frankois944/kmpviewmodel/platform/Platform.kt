package fr.frankois944.kmpviewmodel.platform

import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UISceneActivationStateForegroundActive
import platform.UIKit.UIViewController
import platform.UIKit.UIWindowScene

internal class Platform(
    override val isDebug: Boolean,
    override val isProduction: Boolean,
) : IPlatform {
    override val platformContext: UIViewController?
        get() {
            return UIApplication.sharedApplication
                .connectedScenes
                .mapNotNull { it as? UIWindowScene }
                .firstOrNull { it.activationState == UISceneActivationStateForegroundActive }
                ?.keyWindow
                ?.rootViewController
        }

    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}
