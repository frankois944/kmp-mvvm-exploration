//
//  MyFirstScreenWithoutMacro.swift
//  iosApp
//
//  Created by François Dabonot on 31/05/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import Foundation
import SwiftUI
import Shared

struct MyFirstScreenWithSkie: View {

    @StateObject private var viewModel: SharedViewModel<MainScreenViewModel>
    @State private var mainScreenUIState: MainScreenUIState = .Loading()
    @State private var userId: String?
    @State private var jobDisposeBag = CoroutineJobDisposeBag()
    @State private var fruits = [FruitData]()
    @State private var events: MyFirstScreenUiEvents?
    let onNextView: () -> Void

    init(param1: String? = nil, onNextView: @escaping () -> Void) {
        _viewModel = StateObject(wrappedValue: { .init(parameters: ["IOS-MyFirstScreenWithSkie"]) }())
        self.onNextView = onNextView
    }

    var body: some View {
        MyFirstView(mainScreenUIState: mainScreenUIState,
                    userId: userId,
                    fruits: fruits,
                    events: $events)
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
            .collect(flow: viewModel.instance.mainScreenUIState, into: $mainScreenUIState) {
                print("COLLECTING mainScreenUIState : \(String(describing: $0))")
                return $0
            }
            .collect(flow: viewModel.instance.userId, into: $userId) {
                print("COLLECTING userId : \(String(describing: $0))")
                return $0
            }
            .collect(flow: viewModel.instance.datasource, into: $fruits) {
                print("COLLECTING fruits : \(String(describing: $0))")
                return $0
            }
    }
}
