//
//  KoinHelper.swift
//  iosApp
//
//  Created by François Dabonot on 26/06/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import Foundation

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
        // check if T is a Class or a Protocol
        let protocolType = NSProtocolFromString("\(T.self)")
        
        if let ktClass =  protocolType != nil ?
            // resolve KClass by Protocol
            Shared.getOriginalKotlinClass(objCProtocol: protocolType!) :
                // resolve KClass by Clacs
                Shared.getOriginalKotlinClass(objCClass: T.self) {
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
        fatalError("Cant resolve Koin Injection \(self)")
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

