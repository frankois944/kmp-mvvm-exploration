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
    private let logger = log(tag: "FirstScreenDataStore")
    private let accountService = AccountService(logger: log(tag: "AccountService"))
    private let profilService = ProfileService(logger: log(tag: "ProfileService"))
    private var disposebag = Set<AnyCancellable>()
    
    @Published var mainScreenUIState: MainScreenUIState = .Loading()
    @Published var userId: String?
    
    init(param1: String?) {
        logger.d(messageString: "INIT")
        self.param1 = param1
        self.disposebag.insert(AppContext.shared.$userId
            .receive(on: DispatchQueue.main)
            .sink { [weak self] in
                self?.userId = $0
        })
    }
    
    func updateUserId() {
        logger.d(messageString: "updateUserId")
        AppContext.shared.userId = "\(Int.random(in: 1..<Int.max))"
    }
    
    func loadData() async {
        do {
            logger.d(messageString: "START LOADING SCREEN")
            self.mainScreenUIState = .Loading()
            let accoundData = try await accountService.getAccountInfo()
            let profileData = try await profilService.getProfile()
            try await Task.sleep(nanoseconds: 3_000_000_000)
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


struct MyFirstScreenWithSwiftDataStore: View {
    @StateObject var viewModel = FirstScreenViewModel(param1: nil)
    
    var body: some View {
        VStack {
            MyFirstView(mainScreenUIState: viewModel.mainScreenUIState,
                        userId: viewModel.userId,
                        updateUserId: viewModel.updateUserId,
                        retry: {})
        }
        .task {
            await viewModel.loadData()
        }
    }
}

#Preview {
    MyFirstScreenWithSwiftDataStore()
}
