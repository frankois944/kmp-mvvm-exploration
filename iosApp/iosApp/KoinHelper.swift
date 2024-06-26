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

extension ViewModel {
    static func getInstance(qualifier: String? = nil, parameters: [Any]? = nil) -> Self {
        if let ktClass = Shared.getOriginalKotlinClass(objCClass: Self.self) {
            var koinQualifier: Koin_coreQualifier?
            if let qualifier = qualifier {
                koinQualifier = KoinQualifier(value: qualifier)
            }
            
            if let instance = AppContext.shared.koinApplication?.koin.get(clazz: ktClass,
                                                                          qualifier: koinQualifier,
                                                                          parameters: {
                .init(_values: .init(array: parameters ?? []), useIndexedValues: true)
            }) {
                return instance as! Self
            }
        }
        fatalError("Cant resolve ViewModel \(self)")
    }
}
