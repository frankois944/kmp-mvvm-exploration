//
//  MyFirstScreen.swift
//  iosApp
//
//  Created by François Dabonot on 24/04/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
import Shared
import KTViewModelBuilder
import Combine

struct MyFirstView: View {
    let mainScreenUIState: MainScreenUIState
    let userId: String?
    let updateUserId: () -> Void
    let retry: () -> Void
    
    var body: some View {
        VStack {
            switch onEnum(of: mainScreenUIState) {
            case .loading:
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())
            case .error(let error):
                Text("Error : \(error.message)")
                    .foregroundStyle(.red)
                Button(action: retry) {
                    Text("RETRY")
                }
            case .success(let success):
                HStack {
                    Text("Bonjour \(userId ?? "NULL"): ")
                    Text(success.profile.username)
                        .fontWeight(.bold)
                }
                Button("RANDOM", action: updateUserId)
                Text("Vos transactions")
                List(success.account.transaction, id: \.self) { transaction in
                    /*NavigationLink(destination: MyFirstScreenWithoutMacro(viewModel: .init(.init(param1: "4242")))) {
                        Text(transaction)
                            .fontWeight(.semibold)
                    }*/
                    NavigationLink(destination: MyFirstScreenWithSwiftDataStore(viewModel: .init(param1: "4242"))) {
                        Text(transaction)
                            .fontWeight(.semibold)
                    }
                }
            }
        }
    }
}


#Preview("LOADING") {
    MyFirstView(mainScreenUIState: .Loading(), userId: "", updateUserId: {}, retry: {})
}

#Preview("ERROR") {
    MyFirstView(mainScreenUIState: .Error(message: "An error"), userId: "", updateUserId: {}, retry: {})
}

#Preview("FAILED") {
    MyFirstView(mainScreenUIState: .Success(profile: .init(username: "Joker"),
                                            account: .init(transaction: ["Tr1", "Tr2"])),
                userId: "",
                updateUserId: {},
                retry: {})
}