//
//  MyFirstScreenWithSwiftSkieIOS14.swift
//  iosApp
//
//  Created by François Dabonot on 07/08/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI

struct MyFirstScreenWithSkieIOS14: View {

    @StateObject private var viewModel: SharedViewModel<MainScreenViewModel>
    @State private var mainScreenUIState: MainScreenUIState = .Loading()
    @State private var userId: String?
    @State private var jobDisposeBag = CoroutineJobDisposeBag()
    @State private var events: MyFirstScreenUiEvents?
    @State var disposebag = Set<Task<(), Never>>()
    let onNextView: () -> Void

    init(param1: String? = nil, onNextView: @escaping () -> Void) {
        _viewModel = StateObject(wrappedValue: { .init(parameters: ["IOS-MyFirstScreenWithSkieIOS14"]) }())
        self.onNextView = onNextView
    }

    var body: some View {
        MyFirstView(mainScreenUIState: mainScreenUIState,
                    userId: userId,
                    events: $events)
            .collect(
                flow: viewModel.instance.userId,
                into: $userId,
                disposedBy: $disposebag
            )
            .collect(
                flow: viewModel.instance.mainScreenUIState,
                into: $mainScreenUIState,
                disposedBy: $disposebag
            )
            .onDisappear(perform: {
                disposebag.removeAll()
            })
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
                case .none:
                    break
                }
            })

    }
}
