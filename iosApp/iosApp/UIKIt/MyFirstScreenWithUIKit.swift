//
//  MyFirstScreenUIKitView.swift
//  iosApp
//
//  Created by Francois Dabonot on 14/07/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import Foundation
import SwiftUI

// Create a UIViewController for SwiftUI
struct MyFirstScreenWithUIKit: UIViewControllerRepresentable {
    typealias UIViewControllerType = MyFirstScreenViewController

    let param1: String?
    let onNextView: () -> Void

    init(param1: String? = nil, onNextView: @escaping () -> Void) {
        self.param1 = param1
        self.onNextView = onNextView
    }

    func makeUIViewController(context: Context) -> MyFirstScreenViewController {
        let newVc = (UIStoryboard(name: "MyFirstScreen", bundle: nil).instantiateInitialViewController()
                        as? MyFirstScreenViewController)!
        newVc.param1 = param1
        newVc.onNextView = onNextView
        return newVc
    }

    func updateUIViewController(_ uiViewController: MyFirstScreenViewController, context: Context) {
        // Updates the state of the specified view controller with new information from SwiftUI.
    }
}
