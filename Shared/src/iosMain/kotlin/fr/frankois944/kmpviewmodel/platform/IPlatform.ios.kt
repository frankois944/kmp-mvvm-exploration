@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package fr.frankois944.kmpviewmodel.platform

import org.koin.core.annotation.Single
import platform.UIKit.UIViewController

public actual typealias NativeContext = UIViewController

@Single
public actual class Platform2 : IPlatform {
    actual override val name: String = ""
    actual override val isDebug: Boolean = false
    actual override val isProduction: Boolean = false
    actual override val platformContext: NativeContext? = null
}
