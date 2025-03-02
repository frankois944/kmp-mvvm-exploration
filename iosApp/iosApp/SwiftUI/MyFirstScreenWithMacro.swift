//
//  MyFirstScreenWithMacro.swift
//  iosApp
//
//  Created by François Dabonot on 30/05/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
import KTViewModelBuilder
import Shared

@ktViewModelBinding(ofType: MainScreenViewModel.self,
                    publishing:
                        .init(\.mainScreenUIState, MainScreenUIState.self),
                    .init(\.userId, String?.self),
                    .init(\.intNotNullValue, Int.self),
                    .init(\.intNullValue, Int?.self),
                    .init(\.datasource, [FruitData].self)
)
class MyMainScreenViewModel: ObservableObject {}

struct MyFirstScreenWithMacro: View {

    @StateObject private var viewModel = MyMainScreenViewModel(koinGet(parameters: ["IOS-MyFirstScreenWithMacro"]))
    @State private var jobDisposeBag = CoroutineJobDisposeBag()
    @State private var events: MyFirstScreenUiEvents?
    let onNextView: () -> Void

    var body: some View {
        VStack {
            MyFirstView(mainScreenUIState: viewModel.mainScreenUIState,
                        userId: viewModel.userId,
                        fruits: viewModel.datasource,
                        events: $events)
        }
        .onDisappear {
            // Disposing is useful when the View is not destroyed when pop from the navigation Stack
            // For example: using NavigationView instead of NavigationStack
            jobDisposeBag.dispose()
        }
        .onChange(of: events, perform: {
            switch onEnum(of: $0) {
            case .retry:
                viewModel.instance
                    .reload()
                    .store(in: &jobDisposeBag)
            case .updateUserId:
                viewModel.instance.updateUserId()
            case .nextView:
                onNextView()
            case .addNewFruit:
                viewModel.instance.addRandomFruitToDatabase()
            case .removeAllFruit:
                viewModel.instance.removeAllFruitFromDatabase()
            case .none:
                break
            }
        })
        .task {
            await viewModel.start()
        }
    }
}
