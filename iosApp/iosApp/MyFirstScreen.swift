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

struct MyFirstScreenWithoutMacro: View {
    @StateObject var viewModel: SharedViewModel<MainScreenViewModel> = .init(.init(param1: nil))
    @State var mainScreenUIState: MainScreenUIState = .Loading()
    @State var userId: String?
    
    var body: some View {
        MyFirstView(mainScreenUIState: mainScreenUIState,
                    userId: userId,
                    updateUserId: viewModel.instance.updateUserId,
                    retry: viewModel.instance.reload)
        .collect(flow: viewModel.instance.mainScreenUIState, into: $mainScreenUIState) {
            print("COLLECTING mainScreenUIState : \(String(describing: $0))")
            return $0
        }
        .collect(flow: viewModel.instance.userId, into: $userId) {
            print("COLLECTING userId : \(String(describing: $0))")
            return $0
        }
    }
}

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
            case .idle:
                EmptyView()
            }
        }
    }
}


#Preview {
    MyFirstView(mainScreenUIState: .Loading(), userId: "", updateUserId: {}, retry: {})
}

#Preview {
    MyFirstView(mainScreenUIState: .Error(message: "An error"), userId: "", updateUserId: {}, retry: {})
}

#Preview {
    MyFirstView(mainScreenUIState: .Success(profile: .init(username: "Joker"),
                                            account: .init(transaction: ["Tr1", "Tr2"])),
                userId: "",
                updateUserId: {},
                retry: {})
}
