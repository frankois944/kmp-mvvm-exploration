# Exploration of KMP MVVM and other useful features for iOS developer

I'm trying to find a good solution for using the MVVM pattern with the KMP ViewModel on SwiftUI/UIKit.

It's not that simple, I'm working on it for some time and with the advancement of KMP, it sounds to be easier but not so much :)

The KMP MVVM approach on [Android is fully supported](https://developer.android.com/topic/libraries/architecture/viewmodel), using Kotlin multiplatform or not, it's the same implementation.

Otherwise, on iOS, it's kind of experimental, the KMP ViewModel is not made to work on this target, we need to find some workaround for correctly using it.

Other features presented in this repository are optional for using MVVM pattern, but I think it's kind of useful.

You will find inside this repo :

- [Requirement](#mvvm-concept-on-ios)
- [MVVM with different approach](#the-viewmodel)
    - [Skie observable](#mvvm-using-skie-observable)
    - [Custom macro](#mvvm-using-macro)
    - [SwiftUI MVVM](#classic-mvvm)
    - [UIKit](#uikit)
- Injection with [Koin annotation](https://insert-koin.io/)
- [Getting the viewmodel or any instance from Swift/Koin](#getting-the-viewmodel-or-any-instance-from-swift-and-koin)
- Logging with [Kermit](https://kermit.touchlab.co/)
- Usage of [DataStore](https://developer.android.com/jetpack/androidx/releases/datastore)
- and more little experiences

## MVVM concept on IOS

So, the most interesting things is about the MVVM :

Inspiration from this repository https://github.com/joreilly/FantasyPremierLeague and this issue https://github.com/joreilly/FantasyPremierLeague/issues/231

- First step is exporting the Kotlin mvvm dependency to Swift

```gradle
it.binaries.framework {
    //...
    export(libs.androidx.lifecycle.viewmodel)
}
```

- Then importing [SKIE](https://skie.touchlab.co/) to fully access the Kotlin Flow from Swift

And activate some useful features :

```gradle
skie {
    features {
        // https://skie.touchlab.co/features/flows-in-swiftui
        enableSwiftUIObservingPreview = true
        // https://skie.touchlab.co/features/combine
        enableFutureCombineExtensionPreview = true
        enableFlowCombineConvertorPreview = true
    }
}
```
 
- Finally creating a SwiftUI class to manage the KMP viewmodel lifecycle 
```swift
class SharedViewModel<VM : ViewModel> : ObservableObject {
    
    private let key = String(describing: type(of: VM.self))
    private let viewModelStore = ViewModelStore()
    
    // Injecting the viewmodel
    init(_ viewModel: VM = .init()) {
        viewModelStore.put(key: key, viewModel: viewModel)
    }

    // Optional: Creating the viewmodel from compatible koin parameters
    init(qualifier: String? = nil, parameters: [Any]? = nil) {
        let viewmodel = VM.get(qualifier: qualifier, parameters: parameters)
        viewModelStore.put(key: key, viewModel: viewmodel)
    }
    
    var instance: VM {
        viewModelStore.get(key: key) as! VM
    }
    
    deinit {
        viewModelStore.clear()
    }
}
```
### The ViewModel

Based on this shared Kotlin [viewmodel](https://github.com/frankois944/kmp-mvvm-exploration/blob/main/Shared/src/commonMain/kotlin/fr/frankois944/kmpviewmodel/viewmodels/mainscreen/MainScreenViewModel.kt).

Also, you can find the android integration [here](https://github.com/frankois944/kmp-mvvm-exploration/blob/main/androidApp/src/main/java/fr/frankois944/kmpviewmodel/android/MyFirstScreen.kt).

### MVVM using Skie observable

[Example with SKIE](https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/SwiftUI/MyFirstScreenWithSkie.swift).

This approach is using the [SKIE flow SwiftUI capability](https://skie.touchlab.co/features/flows-in-swiftui) 

### MVVM using Macro

[Example with a macro](https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/SwiftUI/MyFirstScreenWithMacro.swift), more like an iOS dev will commonly use.

This approach is using a [macro I made](https://github.com/frankois944/kmp-mvvm-exploration/tree/main/KTViewModelBuilder) to automatically wrap a KMP viewmodel inside an ObservableObject, almost like a SwiftUI viewmodel.

### Classic MVVM

[Example with a common SwiftUI ViewModel](https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/SwiftUI/MyFirstScreenWithSwiftViewModel.swift)

No usage of KMP MVVM here, just like a MVVM SwiftUI class, but we need to use the Koin injection capacities.

### UIKit

[Example with a UIViewController](https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/UIKIt/MyFirstScreenViewController.swift)

UIKit is not dead, we can use the `SharedViewModel` class and the [SKIE combine support ](https://skie.touchlab.co/features/combine)

## Thinking

The goal of this experiment is to align the behavior between Android ViewModel and SwiftUI ViewModel, it's not that simple as the viewmodel MUST be the same but the lifecycle of View holder are different.

Look at the logs I added to verify the lifecycle, it should be exactly the same on the different approach.

## Getting the viewmodel or any instance from Swift and Koin

As this playground is using Koin, I want to get my viewmodel from it, not on direct way with the constructor (it's still working, but it's not great).

So we can use Koin qualifier and parameters like Koin for Android.

- We need to export an important kotlin method which resolve ObjC class to Kotlin Class from Swift Application

https://github.com/frankois944/kmp-mvvm-exploration/blob/main/Shared/src/iosMain/kotlin/fr/frankois944/kmpviewmodel/AppInit.ios.kt

- Store the Kotlin Koin Context somewhere and make it accessible everywhere in the swift App
```swift
// For example: store in swift singleton the koin application
AppContext.shared.koinApplication = // instance of initialized koinapplication
```
- [Then create some swift helpers](https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/KoinHelper.swift)
```swift
private class KoinQualifier: Koin_coreQualifier {
    init(value: String) {
        self.value = value
    }
    var value: String
}

extension Koin_coreKoinApplication {

    // reproducing the koin `get()` method behavior
    // we can set qualifier and parameters
    func get<T: AnyObject>(qualifier: String? = nil, parameters: [Any]? = nil) -> T {
        let ktClass: KotlinKClass?
        // check if T is a Class or a Protocol and get the linked kotlin class
        if let protocolType = NSProtocolFromString("\(T.self)") {
            ktClass = Shared.getOriginalKotlinClass(objCProtocol: protocolType)
        } else {
            ktClass = Shared.getOriginalKotlinClass(objCClass: T.self)
        }

        guard let ktClass else {
            // no Kotlin Class found, it's an critical error
            fatalError("Cant resolve objc class \(T.self)")
        }

        var koinQualifier: Koin_coreQualifier?
        if let qualifier = qualifier {
            koinQualifier = KoinQualifier(value: qualifier)
        }

        var koinParameters: (() -> Koin_coreParametersHolder)?
        if let parameters {
            koinParameters = {
                .init(_values: .init(array: parameters), useIndexedValues: nil)
            }
        }

        guard let instance = koin.get(clazz: ktClass,
                                      qualifier: koinQualifier,
                                      parameters: koinParameters) as? T else {
            fatalError("Cant resolve Koin Injection \(self)")
        }
        return instance
    }
}

/// propertyWrapper like `by inject()` koin method
@propertyWrapper struct KoinInject<T: AnyObject> {
    var qualifier: String?
    var parameters: [Any]?

    init(qualifier: String? = nil, parameters: [Any]? = nil) {
        self.qualifier = qualifier
        self.parameters = parameters
    }

    lazy var wrappedValue: T = {
        return koinGet(qualifier: qualifier, parameters: parameters)
    }()
}

/// like the `get()` koin method
func koinGet<T: AnyObject>(qualifier: String? = nil, parameters: [Any]? = nil) -> T {
    return AppContext.shared.koinApplication.get(qualifier: qualifier,
                                                 parameters: parameters)
}
```
- Finally, get the instance from the koin, ie:
```swift
    // lazy loading of any instance
    @KoinInject<AccountService> private var accountService
    // direct loading of any instance
    private let logger: KermitLogger = koinGet(parameters: ["FirstScreenDataStore"])

    // get the viewmodel as example
    @StateObject private var viewModel: SharedViewModel<MainScreenViewModel>
    init(param1: String? = nil) {
        _viewModel = StateObject(wrappedValue: { .init(parameters: ["IOS-MyFirstScreenWithoutMacro"]) }())
    }
    // or
    @StateObject private var viewModel: SharedViewModel<MainScreenViewModel> = .init(koinGet())
    // and many other ways
```
