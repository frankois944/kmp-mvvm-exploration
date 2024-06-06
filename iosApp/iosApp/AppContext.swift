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

class AppContext : ObservableObject {
    
    // MARK: Properties
    
    static let shared = AppContext()
    
    // MARK: Private
    
    private let common = Shared.AppContext.companion.instance
    private let logger = log(tag: "AppContext")
    private var disposebag = Set<AnyCancellable>()
    
    // MARK: Public
    @Published var username: String? {
        didSet {
            common.username = username
        }
    }
    
    @Published var sessionToken: String? {
        didSet {
            common.sessionToken = sessionToken
        }
    }
    @Published var userId: String?  {
        didSet {
            common.userId = userId
        }
    }
    @Published private var isDebug: Bool = false
    @Published private(set) var isProduction: Bool = false
    
    // MARK: - Methods
    
    // MARK: Init
    
    init() {
        logger.d(messageString: "INIT APPCONTEXT")
        /*isDebug = common.platform.isDebug
         isProduction = common.platform.isProduction*/
        disposebag.insert(
            common.usernameFlow.toPublisher()
                .receive(on: DispatchQueue.main)
                .sink { [weak self] in
                    self?.logger.i(messageString: "UPDATING username with \(String(describing: $0))")
                    self?.username = $0
                }
        )
        disposebag.insert(
            common.userIdFlow.toPublisher()
                .receive(on: DispatchQueue.main)
                .sink { [weak self] in
                    self?.logger.i(messageString: "UPDATING userId with \(String(describing: $0))")
                    self?.userId = $0
                }
        )
        disposebag.insert(
            common.sessionTokenFlow.toPublisher()
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



