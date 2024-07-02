//
//  iOSApp.swift
//  iosApp
//
//  Created by François Dabonot on 24/04/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
import KTViewModelBuilder
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
    let appContext: AppContext
    
    @State var logger: KermitLogger
    @State var router = NavigationPath()
    
    init() {
#if DEBUG
        let koin = AppInitKt.startApp(appConfig: .init(isDebug: true, isProduction: false))
#else
        let koin = AppInitKt.startApp(appConfig: .init(isDebug: false, isProduction: false))
#endif
        appContext = .init(koinApplication: koin)
        logger = koinGet(parameters: ["iOSApp"])
    }
    
    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $router) {
                MyFirstScreenWithSwiftViewModel()
                    .navigationDestination(for: NavRoute.SecondScreen.self) { value in
                        MyFirstScreenWithSwiftViewModel()
                    }
            }
            .environmentObject(appContext)
            .onReceive(NotificationCenter.default.publisher(for: notification),
                       perform: {
                logger.i(messageString: "Notification received : \($0)")
            })
        }
    }
}

