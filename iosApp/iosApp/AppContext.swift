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
import Shared

/// Store global properties and context
///
/// **AppContext.configure** must be called in first place
class AppContext: ObservableObject {

    // MARK: Properties

    /// Singleton to access the Native/Shared AppContext.
    ///
    /// **AppContext.configure MUST be called before accessing this property**
    nonisolated(unsafe) static var shared: AppContext!

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
    let koinApplication: KoinCoreKoinApplication

    // MARK: - Methods

    // MARK: Init

    /// Create the singleton
    /// - Parameter koinApplication: A Koin application scope
    private init(koinApplication: KoinCoreKoinApplication) {
        self.koinApplication = koinApplication
    }

    /// Initialize all values of the AppContext
    private func setup() {
        logger.d(messageString: "INIT APPCONTEXT")
        common.usernameFlow.toPublisher()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                self?.logger.i(messageString: "UPDATING usernameFlow with \(String(describing: $0))")
                self?.sessionToken = $0
            }
            .store(in: &disposebag)

    }

    /// Create the AppContext Singleton and initialize it
    /// - Parameter koinApplication: A Koin application scope
    @MainActor static func configure(koinApplication: KoinCoreKoinApplication) {
        AppContext.shared = AppContext(koinApplication: koinApplication)
        AppContext.shared.setup()
    }

    // MARK: - DeInit

    deinit {
        d(string: "DEINIT \(self)")
    }
}
