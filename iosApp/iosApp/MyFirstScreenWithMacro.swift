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
    @State private var reloadingTask: Kotlinx_coroutines_coreJob?
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
                reloadingTask = viewModel.instance.reload()
            case .updateUserId:
                viewModel.instance.updateUserId()
            case .nextView:
                onNextView()
            case .none:
                break
            }
        })
        .onDisappear {
            reloadingTask?.cancel(cause: nil)
        }
        .task {
            await viewModel.start()
        }
    }
}
