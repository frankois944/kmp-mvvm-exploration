# 🚀 KMP MVVM Exploration

[![Kotlin](https://img.shields.io/badge/kotlin-2.1.0-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![Swift](https://img.shields.io/badge/swift-6.0-orange.svg?logo=swift)](https://developer.apple.com/swift/)
[![Koin](https://img.shields.io/badge/Koin-4.0.0-purple.svg)](https://insert-koin.io/)
[![SKIE](https://img.shields.io/badge/SKIE-0.9.1-red.svg)](https://skie.touchlab.co/)

A playground exploring the **MVVM pattern** with **KMP ViewModel** on **iOS (SwiftUI & UIKit)**.

> [!IMPORTANT]  
> This project is a **Proof of Concept (PoC)** and a collection of experiments. It is intended for exploration and learning, not as a strict production-ready guide.

---

## 📌 Overview

Implementing MVVM in Kotlin Multiplatform for iOS can be tricky due to **lifecycle management**. While Android has first-class support for `ViewModel`, iOS requires workarounds to ensure proper memory management and lifecycle alignment.

This repository demonstrates various ways to bridge the gap between KMP and iOS.

### 🧪 What's Inside?

- [📋 Requirements](#-requirements)
- [🏗️ The ViewModel Pattern](#️-the-viewmodel-pattern)
- [📱 SwiftUI Integration](#-swiftui-integration)
    - [SKIE Observable (iOS 15+)](#skie-observable-ios-15-and-later)
    - [SKIE Observable (iOS 14)](#skie-observable-ios-14-and-earlier)
    - [Custom Macro Integration](#mvvm-using-macro)
    - [Pure SwiftUI MVVM](#pure-swiftui-mvvm)
- [🏛️ UIKit Support](#️-uikit)
- [💾 Database (Room)](#-database)
- [💉 Dependency Injection (Koin)](#-dependency-injection)
- [📝 Logging & DataStore](#-logging--datastore)

---

## 💡 Pro Tip: Optimize Your iOS Exports

> [!TIP]  
> Use [explicitAPI](https://www.baeldung.com/kotlin/explicit-api-mode) in Gradle to control code visibility.
>
> By default, all public Kotlin code is exported to the iOS framework. Reducing visibility (`internal`, `private`) leads to:
> - ⚡ **Faster build times**
> - 📉 **Smaller binary size**
> - 🚀 **Better Xcode autocomplete performance**

---

## 📋 Requirements

To properly use KMP ViewModels on iOS, we need to handle the export and lifecycle.

### 1. Export Dependencies
In your [shared Gradle file](Shared/build.gradle.kts), you must export the `androidx.lifecycle.viewmodel` so it's accessible in Swift.

```kotlin
kotlin {
    // ...
    cocoapods { // or framework
        export(libs.androidx.lifecycle.viewmodel)
    }
    
    sourceSets {
        commonMain.dependencies {
            api(libs.androidx.lifecycle.viewmodel)
        }
    }
}
```

### 2. Configure SKIE
[SKIE](https://skie.touchlab.co/) is used to bridge Kotlin Flows to Swift effortlessly.

```kotlin
skie {
    features {
        enableSwiftUIObservingPreview = true // For >= iOS 15
        enableFutureCombineExtensionPreview = true
        enableFlowCombineConvertorPreview = true
    }
}
```

### 3. Handle Lifecycle (The SharedViewModel Wrapper)
We wrap the KMP ViewModel in an `ObservableObject` to align its lifecycle with SwiftUI views.

```swift
class SharedViewModel<VM: ViewModel>: ObservableObject {
    private let key = String(describing: type(of: VM.self))
    private let viewModelStore = ViewModelStore()

    init(_ viewModel: VM = .init()) {
        viewModelStore.put(key: key, viewModel: viewModel)
    }

    var instance: VM {
        (viewModelStore.get(key: key) as? VM)!
    }

    deinit {
        viewModelStore.clear() // Triggers onCleared() in Kotlin
    }
}
```

---

## 🏗️ The ViewModel Pattern

The [MainScreenViewModel](Shared/src/commonMain/kotlin/fr/frankois944/kmpviewmodel/viewmodels/mainscreen/MainScreenViewModel.kt) provides a common logic for both platforms.

- **Android:** Direct integration using standard `viewModel()` delegate. [Example](androidApp/src/main/java/fr/frankois944/kmpviewmodel/android/MyFirstScreen.kt).
- **iOS:** Uses SKIE to transform [Kotlin Flows](https://kotlinlang.org/docs/flow.html) into [Swift async/await](https://www.avanderlee.com/swift/async-await/) or Combine.

---

## 📱 SwiftUI Integration

### SKIE Observable (iOS 15 and later)
Uses [SKIE's SwiftUI observing](https://skie.touchlab.co/features/flows-in-swiftui) which leverages the `.task` modifier.
🔗 [Example Code](iosApp/iosApp/SwiftUI/MyFirstScreenWithSkie.swift)

### SKIE Observable (iOS 14 and earlier)
A [custom implementation](iosApp/iosApp/SkieCollectForiOS14.swift) using `.onAppear` for older iOS versions.
🔗 [Example Code](iosApp/iosApp/SwiftUI/MyFirstScreenWithSkieIOS14.swift)

### MVVM using Macro
Uses a [custom Swift Macro](https://github.com/frankois944/KTViewModelBuilder) to automatically wrap KMP ViewModels.
🔗 [Example Code](iosApp/iosApp/SwiftUI/MyFirstScreenWithMacro.swift)

### Pure SwiftUI MVVM
Standard SwiftUI `ObservableObject` using KMP services via Koin, without using KMP `ViewModel`.
🔗 [Example Code](iosApp/iosApp/SwiftUI/MyFirstScreenWithSwiftViewModel.swift)

---

## 🏛️ UIKit Support

UIKit is fully supported by combining `SharedViewModel` with [SKIE Combine extensions](https://skie.touchlab.co/features/combine).
🔗 [Example Code](iosApp/iosApp/UIKIt/MyFirstScreenViewController.swift)

---

## 💾 Database

Example implementation using **Room KMP**.
🔗 [Database Module](Database)

> [!IMPORTANT]  
> Avoid exporting generated database code to iOS. Use a parent module to hide the database implementation details and only export necessary interfaces. Large schemas can significantly impact build times and binary size.

---

## 💉 Dependency Injection

This project uses **Koin** with [Annotations](https://insert-koin.io/docs/reference/koin-annotations/annotations).

### ViewModel Injection Fix
To use Koin ViewModels in a shared module without Compose, add this helper:
🔗 [KMPViewModelAnnotation.kt](Shared/src/commonMain/kotlin/fr/frankois944/kmpviewmodel/di/KMPViewModelAnnotation.kt)

### Accessing Koin from Swift
1. **Export Koin Helpers:** [AppInit.ios.kt](Shared/src/iosMain/kotlin/fr/frankois944/kmpviewmodel/AppInit.ios.kt)
2. **Setup Swift Context:** 
   ```swift
   // Store somewhere inside a singleton/static your Kotlin's KoinApplication instance for later usage
   AppContext.shared.koinApplication = koinApp // Your initialized Koin application
   ```
3. **Use Swift Helpers:** [KoinHelper.swift](iosApp/iosApp/KoinHelper.swift)

#### Usage in Swift:
```swift
// Property Wrapper
@KoinInject private var accountService: AccountService

// Direct Retrieval
let logger: KermitLogger = koinGet(parameters: ["MainScreen"])

// ViewModel with parameters
@StateObject private var viewModel = StateObject(wrappedValue: SharedViewModel(koinGet(parameters: ["ID"])))
```

---

## 📝 Logging & DataStore

- **Logging:** Powered by [Kermit](https://kermit.touchlab.co/).
- **Persistence:** [Jetpack DataStore](https://developer.android.com/jetpack/androidx/releases/datastore) for multiplatform key-value storage.
