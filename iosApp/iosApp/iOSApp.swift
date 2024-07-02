//
//  iOSApp.swift
//  iosApp
//
//  Created by François Dabonot on 24/04/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
@_exported import Shared

extension Error {
    
    var asKotlinThrowable: KotlinThrowable {
        KotlinThrowable(message: self.localizedDescription,
                        cause: KotlinThrowable(message: "\(self)"))
    }
}

@main
struct iOSApp: App {
    
    let notification: Notification.Name = .init(AppEvents.shareContent.name)

    @State var router = NavigationPath()
    
    init() {
#if DEBUG
        let koin = AppInitKt.startApp(appConfig: .init(isDebug: true, isProduction: false))
#else
        let koin = AppInitKt.startApp(appConfig: .init(isDebug: false, isProduction: false))
#endif
        
        AppContext.shared.koinApplication = koin
    }
    
    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $router) {
                MyFirstScreenWithSwiftDataStore()
                    .navigationDestination(for: NavRoute.SecondScreen.self) { value in
                        MyFirstScreenWithSwiftDataStore()
                    }
            }
            .environmentObject(AppContext.shared)
            .onReceive(NotificationCenter.default.publisher(for: notification),
                       perform: {
                log(tag: "iOSApp").i(messageString: "Notification received : \($0)")
            })
        }
    }
}

