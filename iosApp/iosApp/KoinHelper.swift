//
//  KoinHelper.swift
//  iosApp
//
//  Created by François Dabonot on 26/06/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import Foundation
import SwiftUI
import Shared

private class KoinQualifier: KoinCoreQualifier {
    init(value: String) {
        self.value = value
    }
    var value: String
}

extension KoinCoreKoinApplication {

    // reproducing the koin `get()` method behavior
    // we can set qualifier and parameters
    fileprivate func get<T: AnyObject>(qualifier: String? = nil, parameters: [Any]? = nil) -> T {
        let ktClass: KotlinKClass?
        // check if T is a Class or a Protocol and get the linked kotlin class
        if let protocolType = NSProtocolFromString("\(T.self)") {
            ktClass = Shared.getOriginalKotlinClass(objCProtocol: protocolType)
        } else {
            ktClass = Shared.getOriginalKotlinClass(objCClass: T.self)
        }

        guard let ktClass else {
            // no Kotlin Class found, it's an critical error
            fatalError("Cant resolve objc class [Type]:\(T.self)")
        }

        var koinQualifier: KoinCoreQualifier?
        if let qualifier = qualifier {
            koinQualifier = KoinQualifier(value: qualifier)
        }

        var koinParameters: (() -> KoinCoreParametersHolder)?
        if let parameters {
            koinParameters = {
                .init(_values: .init(array: parameters), useIndexedValues: nil)
            }
        }

        guard let instance = koin.get(clazz: ktClass,
                                      qualifier: koinQualifier,
                                      parameters: koinParameters) as? T else {
            fatalError("Cant resolve Koin Injection [Type]:\(T.self) [ktClass]:\(ktClass)")
        }
        return instance
    }
}

/// direct inject of koin Inject (like `get()` koin method)
func koinGet<T: AnyObject>(qualifier: String? = nil, parameters: [Any]? = nil) -> T {
    return AppContext.shared.koinApplication.get(qualifier: qualifier,
                                                 parameters: parameters)
}
