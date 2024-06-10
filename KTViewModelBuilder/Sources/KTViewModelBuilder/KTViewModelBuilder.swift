import SwiftUI

// The Swift Programming Language
// https://docs.swift.org/swift-book

@attached(member, names: arbitrary)
public macro sharedViewModel<T>(ofType: T.Type, publishing: (property: PartialKeyPath<T>, type: Any.Type)...) = #externalMacro(module: "KTViewModelBuilderMacros", type: "SharedViewModelMacro")
