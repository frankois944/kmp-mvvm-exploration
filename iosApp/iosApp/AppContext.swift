//
//  AppContext.swift
//  iosApp
//
//  Created by François Dabonot on 06/05/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import Foundation
import SwiftUI
import Combine

/// if you want to observe the kotlin AppContext, you need to wrap it inside an ObservableObject
/// like a viewmodel
///
/// You can store it inside an `.environmentObject` SwiftUI
class AppContext: ObservableObject {

    // MARK: Properties

    static private var _shared: AppContext?
    /// Singleton to access the Native/Shared AppContext.
    ///
    /// **AppContext.init MUST be called before accessing this property**
    static var shared: AppContext {
        // swiftlint:disable:next identifier_name
        guard let _shared else {
            fatalError("Call AppContext.init first to setup the singleton")
        }
        return _shared
    }

    // MARK: Private

    private lazy var logger: KermitLogger = koinGet(parameters: ["AppContext"])
    private lazy var common: Shared.AppContext = koinGet()
    private var disposebag = Set<AnyCancellable>()

    // MARK: Public

    var sessionToken: String? {
        didSet {
            common.sessionToken = sessionToken
        }
    }
    /// the koin scope application used by the shared code,
    /// It's needed for resolving injected instances from swift code
    let koinApplication: Koin_coreKoinApplication

    // MARK: - Methods

    // MARK: Init

    init(koinApplication: Koin_coreKoinApplication) {
        self.koinApplication = koinApplication
        AppContext._shared = self
        logger.d(messageString: "INIT APPCONTEXT")

        disposebag.insert(
            common.usernameFlow.toPublisher()
                .receive(on: DispatchQueue.main)
                .sink { [weak self] in
                    self?.logger.i(messageString: "UPDATING sessionToken with \(String(describing: $0))")
                    self?.sessionToken = $0
                }
        )
    }

    // MARK: - DeInit

    deinit {
        d(string: "DEINIT \(self)")
        disposebag.forEach {
            $0.cancel()
        }
        disposebag.removeAll()
    }
}
