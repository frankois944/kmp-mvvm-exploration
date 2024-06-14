//
//  MyFirstScreenWithoutMacro.swift
//  iosApp
//
//  Created by François Dabonot on 31/05/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import Foundation
import SwiftUI

struct MyFirstScreenWithoutMacro: View {
    @StateObject var viewModel: SharedViewModel<MainScreenViewModel> = .init(.init(param1: nil))
    @State var mainScreenUIState: MainScreenUIState = .Loading()
    @State var userId: String?
    @State private var reloadingTask: Kotlinx_coroutines_coreJob?
    
    var body: some View {
        MyFirstView(mainScreenUIState: mainScreenUIState,
                    userId: userId,
                    updateUserId: viewModel.instance.updateUserId,
                    retry: {
            self.reloadingTask = viewModel.instance.reload()
        })
        .onDisappear {
            reloadingTask?.cancel(cause: nil)
        }
        .collect(flow: viewModel.instance.mainScreenUIState, into: $mainScreenUIState) {
            print("COLLECTING mainScreenUIState : \(String(describing: $0))")
            return $0
        }
        .collect(flow: viewModel.instance.userId, into: $userId) {
            print("COLLECTING userId : \(String(describing: $0))")
            return $0
        }
    }
}
