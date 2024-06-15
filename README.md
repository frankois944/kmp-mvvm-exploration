# Exploration of KMP MVVM and other stuff for iOS developer

I'm trying to solve the concept of using MVVM pattern with KMP on SwiftUI iOS

It's not that simple, I'm working on it for some times and with the advancement of KMP, it sounds be easier but not so much :)

You will find inside this repo :

- Injection with Koin annotation on KMP project
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

https://github.com/frankois944/kmp-mvvm-exploration/blob/bc18549d9f867d145e5dd0548c7522a962ae762c/Shared/build.gradle.kts#L117-L125
 
- Finally creating a SwiftUI class to manage the KMP viewmodel lifecycle 

https://github.com/frankois944/kmp-mvvm-exploration/blob/da295bdff93b7dafda8e0bf1f0fbb0ce6bc3e257/iosApp/iosApp/SharedViewModel.swift#L11-L27

### MVVM using Skie

https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/MyFirstScreenWithoutMacro.swift

This approach is using the Skie flow SwiftUI capability https://skie.touchlab.co/features/flows-in-swiftui

### MVVM without Skie but Macro

https://github.com/frankois944/kmp-mvvm-exploration/blob/da295bdff93b7dafda8e0bf1f0fbb0ce6bc3e257/iosApp/iosApp/MyFirstScreenWithMacro.swift

This approach is using a macro I made to automatically wrap a KMP viewmodel inside an ObservableObject, almost like a SwiftUI viewmodel.

https://github.com/frankois944/kmp-mvvm-exploration/blob/da295bdff93b7dafda8e0bf1f0fbb0ce6bc3e257/iosApp/iosApp/MyFirstScreenWithMacro.swift#L12-L17

### classic MVVM

https://github.com/frankois944/kmp-mvvm-exploration/blob/main/iosApp/iosApp/MyFirstScreenWithSwiftViewModel.swift

No usage of KMP mvvm, just like a MVVM SwiftUI class

## Thinking

The goal of this experiment is to align the behavior between Android ViewModel and SwiftUI ViewModel, it's not that simple as the viewmodel MUST be the same but the lifecycle of View are different.

Look at the logs I added to verify the lifecycle, it should be exactly the same on the different approach.

