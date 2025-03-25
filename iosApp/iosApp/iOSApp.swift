//
//  iOSApp.swift
//  iosApp
//
//  Created by François Dabonot on 24/04/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
import KTViewModelBuilder
import Shared

class MyImplClass: MyServiceComplement {
    func myCallBack(callback: @escaping (String) -> Void) {
        callback("DATA - myCallBack")
    }

    func myMethod() -> String {
        "DATA - myMethod"
    }

    var myData: String = "DATA - myData"
}

@main
struct IOSApp: App {

    let notification: Notification.Name = .init(AppEvents.shareContent.name)

    @State var logger: KermitLogger
    @State var router = NavigationPath()

    let myClass = MyImplClass()

    init() {
        #if DEBUG
        let koin = AppInitKt.startApp(appConfig: .init(isDebug: true, isProduction: false), myServiceComplement: myClass)
        #else
        let koin = AppInitKt.startApp(appConfig: .init(isDebug: false, isProduction: false), myServiceComplement: myClass)
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
                    MyFirstScreenWithUIKit {
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
