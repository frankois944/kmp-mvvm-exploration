# Exploration of KMP MVVM and other stuff for iOS developer

I'm trying to solve the concept of using MVVM pattern with KMP on SwiftUI iOS

It's not that simple, I'm working on it for some times and with the advancement of KMP, it sounds be easier but not so much :)

You will find inside this repo :

- Injection with Koin annotation
- [Getting the viewmodel or any instance from Swift and Koin](#getting-the-viewmodel-or-any-instance-from-swift-and-koin)
- Logging with Kermit
- Usage of DataStore
- MVVM with different approach
- and more little experiences

The 3 first are the easiest, they are almost fully documented, the fourth is also done on Android but on iOS, it's currently still experimental.

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

    // Creating the viewmodel from compatible koin parameters
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
From this *viewmodel*

https://github.com/frankois944/kmp-mvvm-exploration/blob/main/Shared/src/commonMain/kotlin/fr/frankois944/kmpviewmodel/viewmodels/mainscreen/MainScreenViewModel.kt

### MVVM using Skie observable

https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/MyFirstScreenWithoutMacro.swift

This approach is using the Skie flow SwiftUI capability https://skie.touchlab.co/features/flows-in-swiftui

### MVVM using Macro

https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/MyFirstScreenWithMacro.swift

This approach is using a [macro I made](https://github.com/frankois944/kmp-mvvm-exploration/tree/main/KTViewModelBuilder) to automatically wrap a KMP viewmodel inside an ObservableObject, almost like a SwiftUI viewmodel.

https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/MyFirstScreenWithMacro.swift#L12-L17

### Classic MVVM

https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/MyFirstScreenWithSwiftViewModel.swift

No usage of KMP mvvm, just like a MVVM SwiftUI class

## Thinking

The goal of this experiment is to align the behavior between Android ViewModel and SwiftUI ViewModel, it's not that simple as the viewmodel MUST be the same but the lifecycle of View holder are different.

Look at the logs I added to verify the lifecycle, it should be exactly the same on the different approach.

### Getting the viewmodel or any instance from Swift and Koin

As this playground is using Koin, I want to get my viewmodel from koin, not on direct way (but it's still working)

So we can use koin qualifier and parameters like koin for Android.

- We need to export an important kotlin method
https://github.com/frankois944/kmp-mvvm-exploration/blob/93718471ebba46ef69f58790f5405f6b1e4b90ee/Shared/src/iosMain/kotlin/fr/frankois944/kmpviewmodel/AppInit.ios.kt#L12

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
        if let ktClass = Shared.getOriginalKotlinClass(objCClass: T.self) {
            var koinQualifier: Koin_coreQualifier?
            if let qualifier = qualifier {
                koinQualifier = KoinQualifier(value: qualifier)
            }
            
            if let instance = koin.get(clazz: ktClass,
                                       qualifier: koinQualifier,
                                       parameters: {
                .init(_values: .init(array: parameters ?? []), useIndexedValues: true)
            }) {
                return instance as! T
            }
        }
        fatalError("Cant resolve ViewModel \(self)")
    }
}

/// lazy inject of koin injection (like `by inject()` koin method)
@propertyWrapper struct KoinInject<T: AnyObject> {
    var qualifier: String? = nil
    var parameters: [Any]? = nil
    
    init(qualifier: String? = nil, parameters: [Any]? = nil) {
        self.qualifier = qualifier
        self.parameters = parameters
    }
    
    lazy var wrappedValue: T = {
        return koinGet(qualifier: qualifier, parameters: parameters)
    }()
}

/// direct inject of koin Inject (like `get()` koin method)
func koinGet<T: AnyObject>(qualifier: String? = nil, parameters: [Any]? = nil) -> T {
    guard let koinApplication = AppContext.shared.koinApplication else {
        fatalError("Cant get koinApplication")
    }
    return koinApplication.get(qualifier: qualifier, parameters: parameters)
}
```
- Finally, get the instance from the koin, ie:
```swift
    // lazy loading of any instance
    @KoinInject<AccountService> private var accountService
    // direct loading of any instance
    private let logger: KermitLogger = koinGet(parameters: ["FirstScreenDataStore"])

    // direct loading of a viewmodel as exemple
    @StateObject private var viewModel: SharedViewModel<MainScreenViewModel>
    init(param1: String? = nil) {
        _viewModel = StateObject(wrappedValue: { .init(parameters: ["IOS-MyFirstScreenWithoutMacro"]) }())
    }
    // or
    @StateObject private var viewModel: SharedViewModel<MainScreenViewModel> = .init(koinGet())
    // and many other ways
```
