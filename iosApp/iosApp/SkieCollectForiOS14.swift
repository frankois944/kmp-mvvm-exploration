//
//  SkieCollectForiOS14.swift
//  iosApp
//
//  Created by François Dabonot on 08/08/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
import Shared

// An extension of the SwiftUI.View with a compatible iOS 13/14 SKIE `collect` implementation
// It's a copy of the current `collect` implementation of SKIE flow in SwiftUI
// I added a `disposedBy` for managing the lifecycle of the Task correctly
// This way, it sounds like Rx or `Combine` programming
extension SwiftUI.View {

    public func collect<Flow: SkieSwiftFlowProtocol>(flow: Flow,
                                                     into binding: SwiftUI.Binding<Flow.Element>,
                                                     disposedBy: Binding<Set<Task<(), Never>>>) -> some SwiftUI.View {
        collect(flow: flow, disposedBy: disposedBy) { newValue in
            binding.wrappedValue = newValue
        }
    }

    public func collect<Flow: SkieSwiftFlowProtocol, U>(
        flow: Flow,
        into binding: SwiftUI.Binding<U>,
        disposedBy: Binding<Set<Task<(), Never>>>,
        transform: @escaping (Flow.Element) async -> U?
    ) -> some SwiftUI.View {
        collect(flow: flow, disposedBy: disposedBy) { newValue in
            if let newTransformedValue = await transform(newValue) {
                binding.wrappedValue = newTransformedValue
            }
        }
    }

    func collect<Flow: SkieSwiftFlowProtocol>(flow: Flow,
                                              disposedBy: Binding<Set<Task<(), Never>>>,
                                              perform: @escaping (Flow.Element) async -> Swift.Void) -> some SwiftUI.View {
        onAppear {
            disposedBy.wrappedValue.insert(Task {
                do {
                    for try await item in flow {
                        #if DEBUG
                        print("COLLECTING \(item)")
                        #endif
                        await perform(item)
                    }
                } catch {
                    print("error") // or something else
                }
            })
        }
    }
}
