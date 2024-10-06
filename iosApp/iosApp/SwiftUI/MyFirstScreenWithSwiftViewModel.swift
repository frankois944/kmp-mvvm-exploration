//
//  MyFirstScreenWithSwiftDataStore.swift
//  iosApp
//
//  Created by François Dabonot on 30/05/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
import Combine
@preconcurrency import Shared

@MainActor
class FirstScreenViewModel: ObservableObject {

    private let param1: String?
    private let logger: KermitLogger = koinGet(parameters: ["FirstScreenViewModel"])
    private var disposebag = Set<AnyCancellable>()
    private lazy var accountService: IAccountService = koinGet()
    private lazy var profilService: IProfileService = koinGet()
    private lazy var eventBus: IEventBus = koinGet()
    private lazy var appContext: Shared.AppContext = koinGet()

    @Published var mainScreenUIState: MainScreenUIState = .Loading()
    @Published var userId: String?

    init(param1: String?) {
        self.param1 = param1
        logger.d(messageString: "INIT")
        appContext.usernameFlow
            .toPublisher()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                self?.userId = $0
            }
            .store(in: &disposebag)
    }

    func updateUserId() {
        logger.d(messageString: "updateUserId")
        appContext.username = "\(Int.random(in: 1..<Int.max))"
        eventBus.publish(name: .shareContent, value: nil)
    }

    func loadData(reloading: Bool = false) async {
        if !reloading && self.mainScreenUIState is MainScreenUIState.Success {
            return
        }
        logger.d(messageString: "START LOADING SCREEN")
        self.mainScreenUIState = .Loading()
        do {
            // detach the data fetch from the main actor and avoid concurrency issues
            self.mainScreenUIState = try await Task<MainScreenUIState, Error>.detached {
                do {
                    try await Task.sleep(nanoseconds: 3_000_000_000)
                    // throw NSError(domain: "TESTING", code: 42) for testing
                    let accoundData = try await self.accountService.getAccountInfo()
                    let profileData = try await self.profilService.getProfile()
                    await self.logger.d(messageString: "OK LOADING SCREEN")
                    return MainScreenUIState.Success(profile: profileData, account: accoundData)
                } catch {
                    await self.logger.e(messageString: "FAILING LOADING SCREEN")
                    return MainScreenUIState.Error(message: "Something bad \(error)")
                }
            }.value
        } catch {
            self.logger.e(messageString: "FAILING LOADING SCREEN")
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
    @State private var events: MyFirstScreenUiEvents?
    let onNextView: () -> Void

    var body: some View {
        VStack {
            MyFirstView(mainScreenUIState: viewModel.mainScreenUIState,
                        userId: viewModel.userId,
                        events: $events)
        }
        .onChange(of: events, perform: {
            switch onEnum(of: $0) {
            case .retry:
                reloadingTask.insert(Task {
                    await viewModel.loadData(reloading: true)
                })
            case .updateUserId:
                viewModel.updateUserId()
            case .nextView:
                onNextView()
            case .none:
                break
            }
        })
        .onDisappear {
            // Canceling is useful when the View is not destroyed when pop from the navigation Stack
            // For example: using NavigationView instead of NavigationStack
            reloadingTask.forEach { $0.cancel() }
        }
        .task {
            await viewModel.loadData()
        }
    }
}

#Preview {
    MyFirstScreenWithSwiftViewModel {}
}
