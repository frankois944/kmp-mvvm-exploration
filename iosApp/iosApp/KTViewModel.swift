//
//  KTViewModel.swift
//  iosApp
//
//  Created by François Dabonot on 23/05/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import Foundation
import Shared
import KTViewModelBuilder

class KTViewModel<T : Lifecycle_viewmodelViewModel> : ObservableObject {
    
    let instance: T
    
    init(_ viewModel: T) {
        self.instance = viewModel
    }
    
    deinit {
        self.instance.cancelViewModelScope()
        self.instance.onCleared()
    }
}

class KTViewModel2 : ObservableObject {
    
    let instance: MainScreenViewModel
    
    private var jobs = Set<Task<(), Never>>()
    
    @Published var mainScreenUIState: MainScreenUIState
    @Published var userId: String?
    
    init(_ viewModel: MainScreenViewModel) {
        self.instance = viewModel
        self.mainScreenUIState = instance.mainScreenUIState.value
        self.userId = instance.userId.value
        
        jobs.insert(Task {
            for await flowValue in instance.mainScreenUIState {
                mainScreenUIState = flowValue
            }
        })
        jobs.insert(Task {
            for await flowValue in instance.userId {
                userId = flowValue
            }
        })
    }
    
    deinit {
        jobs.forEach {
            $0.cancel()
        }
        jobs.removeAll()
        self.instance.cancelViewModelScope()
        self.instance.onCleared()
    }
}

