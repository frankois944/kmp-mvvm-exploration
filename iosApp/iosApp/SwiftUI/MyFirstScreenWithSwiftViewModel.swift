//
//  MyFirstScreenWithSwiftDataStore.swift
//  iosApp
//
//  Created by François Dabonot on 30/05/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
import Combine
import Shared

@MainActor
class FirstScreenViewModel: ObservableObject {

    private let param1: String?
    private let logger: KermitLogger = koinGet(parameters: ["FirstScreenViewModel"])
    private var disposebag = Set<AnyCancellable>()
    @KoinInject<Shared.IAccountService> private var accountService
    @KoinInject<Shared.IProfileService> private var profilService
    @KoinInject<Shared.IEventBus> private var eventBus
    @KoinInject<Shared.AppContext> private var appContext

    @Published var mainScreenUIState: MainScreenUIState = .Loading()
    @Published var userId: String?
    @Published var fruits: [FruitData] = []

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
        accountService.getAllFruitsAsFlow()
            .toPublisher()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                self?.fruits = $0
            }
            .store(in: &disposebag)
    }

    func updateUserId() {
        logger.d(messageString: "updateUserId")
        appContext.username = "\(Int.random(in: 1..<Int.max))"
        eventBus.publish(name: .shareContent, value: nil)
    }

    func addRandomValueToDatabase() async {
        let idValue = "\(Int.random(in: 1..<Int.max))"
        try? await accountService.addFruit(name: "Fruit:\(idValue)",
                                           fullName: "FullName:\(idValue)",
                                           calories: "\(idValue)")
    }

    func removeAllValueFromDatabase() async {
        try? await accountService.removeAllFruit()
    }

    func loadData(reloading: Bool = false) async {
        do {
            if !reloading && self.mainScreenUIState is MainScreenUIState.Success {
                return
            }
            logger.d(messageString: "START LOADING SCREEN")
            self.mainScreenUIState = .Loading()
            let profileData = try await profilService.getProfile()
            try await Task.sleep(nanoseconds: 3_000_000_000)
            // throw NSError(domain: "TESTING", code: 42) for testing
            logger.d(messageString: "OK LOADING SCREEN")
            self.mainScreenUIState = .Success(profile: profileData)
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
            case .addNewFruit:
                break
            case .removeAllFruit:
                break
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
