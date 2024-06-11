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
    
    @StateObject var viewModel = MyMainScreenViewModel(.init(param1: nil))
    
    var body: some View {
        VStack {
            MyFirstView(mainScreenUIState: viewModel.mainScreenUIState,
                        userId: viewModel.userId,
                        updateUserId: viewModel.instance.updateUserId,
                        retry: viewModel.instance.reload)
        }
        .task {
            await viewModel.start()
        }
    }
}
