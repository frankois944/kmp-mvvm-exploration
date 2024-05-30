//
//  SharedViewModel.swift
//  iosApp
//
//  Created by François Dabonot on 29/05/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import Foundation

class SharedViewModel<VM : ViewModel> : ObservableObject {
    
    private let key = String(describing: type(of: VM.self))
    private let viewModelStore = ViewModelStore()
    
    init(_ viewModel: VM = .init()) {
        viewModelStore.put(key: key, viewModel: viewModel)
    }
    
    var instance: VM {
        viewModelStore.get(key: key) as! VM
    }
    
    deinit {
        viewModelStore.clear()
    }
}

