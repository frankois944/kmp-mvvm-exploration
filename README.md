# Exploration of KMP MVVM and other stuff for iOS developer

I'm trying to solve the concept of using MVVM pattern with KMP on SwiftUI iOS

It's not that simple, I'm working on it for some times and with the advancement of KMP, it sounds be easier but not so much :)

You will find inside this repo :

- Injection with Koin annotation on KMP project
- Getting KMP ViewModel from swift and Koin 
- Logging with Kermit
- Usage of Android DataStore on KMP
- MVVM with different approch
- and more little experiences

The 3 first are the easiest, they are almost fully documented, the forth is also done on Android but on iOS, it's currently still experimental.

## MVVM concept on IOS

So, the most interesting things is about the MVVM :

Inspiration from this repository https://github.com/joreilly/FantasyPremierLeague and this issue https://github.com/joreilly/FantasyPremierLeague/issues/231

- First step is exporting the Kotlin mvvm dependancy to Swift https://github.com/frankois944/kmp-mvvm-exploration/blob/da295bdff93b7dafda8e0bf1f0fbb0ce6bc3e257/Shared/build.gradle.kts#L38

- Then importing SKIE to fully access the Kotlin Flow from Swift

And activate some usefull features :

https://github.com/frankois944/kmp-mvvm-exploration/blob/bc18549d9f867d145e5dd0548c7522a962ae762c/Shared/build.gradle.kts#L117-L125
 
- Finally creating a SwiftUI class to manage the KMP viewmodel lifecycle 

https://github.com/frankois944/kmp-mvvm-exploration/blob/da295bdff93b7dafda8e0bf1f0fbb0ce6bc3e257/iosApp/iosApp/SharedViewModel.swift#L11-L27

From this *viewmodel*

https://github.com/frankois944/kmp-mvvm-exploration/blob/3a3530e0e700730d6e4d4a981253bd8e2f484f50/Shared/src/commonMain/kotlin/fr/frankois944/kmpviewmodel/viewmodels/mainscreen/MainScreenViewModel.kt

### MVVM using Skie observable

https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/MyFirstScreenWithoutMacro.swift

This approach is using the Skie flow SwiftUI capability https://skie.touchlab.co/features/flows-in-swiftui

### MVVM using Macro

https://github.com/frankois944/kmp-mvvm-exploration/blob/da295bdff93b7dafda8e0bf1f0fbb0ce6bc3e257/iosApp/iosApp/MyFirstScreenWithMacro.swift

This approach is using a [macro I made](https://github.com/frankois944/kmp-mvvm-exploration/tree/main/KTViewModelBuilder) to automatically wrap a KMP viewmodel inside an ObservableObject, almost like a SwiftUI viewmodel.

https://github.com/frankois944/kmp-mvvm-exploration/blob/da295bdff93b7dafda8e0bf1f0fbb0ce6bc3e257/iosApp/iosApp/MyFirstScreenWithMacro.swift#L12-L17

### classic MVVM

https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/MyFirstScreenWithSwiftViewModel.swift

No usage of KMP mvvm, just like a MVVM SwiftUI class

## Thinking

The goal of this experiment is to align the behavior between Android ViewModel and SwiftUI ViewModel, it's not that simple as the viewmodel MUST be the same but the lifecycle of View are different.

Look at the logs I added to verify the lifecycle, it should be exactly the same on the different approach.

### Getting the viewmodel from Swift and Koin

As this playground is using Koin, I want to get my viewmodel from koin, not on direct way (but it's still working)
So we can use koin qualifier and parameters like koin for Android

- We need to export an important kotlin method
https://github.com/frankois944/kmp-mvvm-exploration/blob/93718471ebba46ef69f58790f5405f6b1e4b90ee/Shared/src/iosMain/kotlin/fr/frankois944/kmpviewmodel/AppInit.ios.kt#L12
- Then create some swift helpers
https://github.com/frankois944/kmp-mvvm-exploration/blob/dc95775f62bc87c737402311529729833bda6b1f/iosApp/iosApp/KoinHelper.swift#L11-L36
- Finally, get the instance from the swift views
https://github.com/frankois944/kmp-mvvm-exploration/blob/dc95775f62bc87c737402311529729833bda6b1f/iosApp/iosApp/MyFirstScreenWithoutMacro.swift#L14-L21
  
