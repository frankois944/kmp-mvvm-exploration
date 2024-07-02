//
//  MyFirstScreenWithSwiftDataStore.swift
//  iosApp
//
//  Created by François Dabonot on 30/05/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
import Combine

@MainActor
class FirstScreenViewModel: ObservableObject {
    
    private let param1: String?
    private let logger: KermitLogger = koinGet(parameters: ["FirstScreenViewModel"])
    @KoinInject<Shared.IAccountService> private var accountService
    @KoinInject<Shared.IProfileService> private var profilService
    @KoinInject<Shared.IEventBus> private var eventBus
    @KoinInject<Shared.AppContext> private var appContext
    private var disposebag = Set<AnyCancellable>()
    
    @Published var mainScreenUIState: MainScreenUIState = .Loading()
    @Published var userId: String?
    
    init(param1: String?) {
        self.param1 = param1
        logger.d(messageString: "INIT")
        disposebag.insert(appContext.usernameFlow
            .toPublisher()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                self?.userId = $0
            })
    }
    
    func updateUserId() {
        logger.d(messageString: "updateUserId")
        appContext.username = "\(Int.random(in: 1..<Int.max))"
        eventBus.publish(name: .shareContent, value: nil)
    }
    
    func loadData(reloading: Bool = false) async {
        do {
            if !reloading && self.mainScreenUIState is MainScreenUIState.Success {
                return
            }
            logger.d(messageString: "START LOADING SCREEN")
            self.mainScreenUIState = .Loading()
            let accoundData = try await accountService.getAccountInfo()
            let profileData = try await profilService.getProfile()
            try await Task.sleep(nanoseconds: 3_000_000_000)
            //throw NSError(domain: "TESTING", code: 42) for testing
            logger.d(messageString: "OK LOADING SCREEN")
            self.mainScreenUIState = .Success(profile: profileData, account: accoundData)
        } catch {
            logger.e(messageString: "FAILING LOADING SCREEN")
            self.mainScreenUIState = .Error(message: "Something bad \(error)")
        }
    }
    
    deinit {
        logger.d(messageString: "DEINIT")
    }
    
}


struct MyFirstScreenWithSwiftViewModel: View {
    @StateObject private var viewModel = FirstScreenViewModel(param1: nil)
    @State private var reloadingTask = Set<Task<(), Never>>()
    
    var body: some View {
        VStack {
            MyFirstView(mainScreenUIState: viewModel.mainScreenUIState,
                        userId: viewModel.userId,
                        updateUserId: viewModel.updateUserId,
                        retry: {
                self.reloadingTask.insert(Task {
                    await viewModel.loadData(reloading: true)
                })
            })
        }
        .onDisappear {
            reloadingTask.forEach { $0.cancel() }
        }
        .task {
            await viewModel.loadData()
        }
    }
}

#Preview {
    MyFirstScreenWithSwiftViewModel()
}
