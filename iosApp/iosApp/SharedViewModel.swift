//
//  SharedViewModel.swift
//  iosApp
//
//  Created by François Dabonot on 29/05/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import Foundation

class SharedViewModel<VM: ViewModel>: ObservableObject {

    private let key = String(describing: type(of: VM.self))
    private let viewModelStore = ViewModelStore()

    // Injecting the viewmodel
    init(_ viewModel: VM = .init()) {
        viewModelStore.put(key: key, viewModel: viewModel)
    }

    // Creating the viewmodel from compatible koin parameters
    init(qualifier: String? = nil, parameters: [Any]? = nil) {
        let viewmodel: VM = koinGet(qualifier: qualifier, parameters: parameters)
        viewModelStore.put(key: key, viewModel: viewmodel)
    }

    var instance: VM {
        (viewModelStore.get(key: key) as? VM)!
    }

    deinit {
        viewModelStore.clear()
    }
}
