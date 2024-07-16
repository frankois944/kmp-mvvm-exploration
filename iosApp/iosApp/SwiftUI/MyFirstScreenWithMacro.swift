//
//  MyFirstScreenWithMacro.swift
//  iosApp
//
//  Created by François Dabonot on 30/05/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
import KTViewModelBuilder

@sharedViewModel(ofType: MainScreenViewModel.self,
                 publishing:
                    (\.mainScreenUIState, MainScreenUIState.self),
                 (\.userId, String?.self)
)
class MyMainScreenViewModel: ObservableObject {}

struct MyFirstScreenWithMacro: View {

    @StateObject private var viewModel = MyMainScreenViewModel(koinGet(parameters: ["IOS-MyFirstScreenWithMacro"]))
    @State private var jobDisposable = CoroutineJobDisposeBag()
    @State private var events: MyFirstScreenUiEvents?
    let onNextView: () -> Void

    var body: some View {
        VStack {
            MyFirstView(mainScreenUIState: viewModel.mainScreenUIState,
                        userId: viewModel.userId,
                        events: $events)
        }
        .onDisappear {
            // Disposing is useful when the View is not destroyed when pop from the navigation Stack
            // For example: using NavigationView instead of NavigationStack
            jobDisposable.dispose()
        }
        .onChange(of: events, perform: {
            switch onEnum(of: $0) {
            case .retry:
                viewModel.instance
                    .reload()
                    .store(in: &jobDisposable)
            case .updateUserId:
                viewModel.instance.updateUserId()
            case .nextView:
                onNextView()
            case .none:
                break
            }
        })
        .task {
            await viewModel.start()
        }
    }
}
