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

    @StateObject private var viewModel: SharedViewModel<MainScreenViewModel>
    @State private var mainScreenUIState: MainScreenUIState = .Loading()
    @State private var userId: String?
    @State private var reloadingTask: Kotlinx_coroutines_coreJob?
    @State private var events: MyFirstScreenUiEvents?
    let onNextView: () -> Void

    init(param1: String? = nil, onNextView: @escaping () -> Void) {
        _viewModel = StateObject(wrappedValue: { .init(parameters: ["IOS-MyFirstScreenWithoutMacro"]) }())
        self.onNextView = onNextView
    }

    var body: some View {
        MyFirstView(mainScreenUIState: mainScreenUIState,
                    userId: userId,
                    events: $events)
            .onDisappear {
                reloadingTask?.cancel(cause: nil)
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
