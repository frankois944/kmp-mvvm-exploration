//
//  MyFirstScreenWithSwiftSkieIOS14.swift
//  iosApp
//
//  Created by François Dabonot on 07/08/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
import Combine

// An extension of the SharedViewModel with the Shared KMP ViewModel
// It's a copy of the current `collect` implementation of SKIE flow in SwiftUI
// I added a `disposedBy` for managing the lifecycle of the Task correctly
// This way, it sounds like reactive or combined programming
extension SwiftUI.View {

    public func collect<Flow: SkieSwiftFlowProtocol>(flow: Flow,
                                                     into binding: SwiftUI.Binding<Flow.Element>,
                                                     disposedBy: Binding<Set<Task<(), Never>>>) -> some SwiftUI.View {
        collect(flow: flow, disposedBy: disposedBy) { newValue in
            binding.wrappedValue = newValue
        }
    }

    func collect<Flow: SkieSwiftFlowProtocol>(flow: Flow,
                                              disposedBy: Binding<Set<Task<(), Never>>>,
                                              perform: @escaping (Flow.Element) async -> Swift.Void) -> some SwiftUI.View {
        onAppear {
            disposedBy.wrappedValue.insert(Task {
                do {
                    for try await item in flow {
                        await perform(item)
                    }
                } catch {
                    print("error") // or something else
                }
            })
        }
    }
}

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
                flow: viewModel.instance.mainScreenUIState,
                into: $mainScreenUIState,
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
