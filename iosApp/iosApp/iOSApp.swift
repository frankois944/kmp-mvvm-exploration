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

@main
struct IOSApp: App {

    let notification: Notification.Name = .init(AppEvents.shareContent.name)

    @State var logger: KermitLogger
    @State var router = NavigationPath()

    init() {
        #if DEBUG
        let koin = AppInitKt.startApp(appConfig: .init(isDebug: true, isProduction: false))
        #else
        let koin = AppInitKt.startApp(appConfig: .init(isDebug: false, isProduction: false))
        #endif
        AppContext.configure(koinApplication: koin)
        logger = koinGet(parameters: ["iOSApp"])
    }

    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $router) {
                MyFirstScreenWithSkie {
                    router.append(NavRoute.SecondScreen(userId: "rerteterret"))
                }
                .navigationDestination(for: NavRoute.SecondScreen.self) { value in
                    // MyFirstScreenWithMacro
                    // MyFirstScreenWithSkie
                    // MyFirstScreenWithSwiftViewModel
                    // MyFirstScreenWithUIKit
                    MyFirstScreenWithSkie {
                        router.append(NavRoute.SecondScreen(userId: value.userId))
                    }
                }
            }
            .environmentObject(AppContext.shared)
            .onReceive(NotificationCenter.default.publisher(for: notification),
                       perform: {
                        logger.i(messageString: "Notification received : \($0)")
                       })
        }
    }
}
