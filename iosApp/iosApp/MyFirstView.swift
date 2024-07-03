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
    @Binding var events: MyFirstScreenUiEvents?

    var body: some View {
        VStack {
            switch onEnum(of: mainScreenUIState) {
            case .loading:
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle())
            case .error(let error):
                Text("Error : \(error.message)")
                    .foregroundStyle(.red)
                Button(action: {
                    events = .Retry()
                }, label: {
                    Text("RETRY")
                })
            case .success(let success):
                HStack {
                    Text("Bonjour \(userId ?? "NULL"): ")
                    Text(success.profile.username)
                        .fontWeight(.bold)
                }
                Button("RANDOM", action: { events = .UpdateUserId(value: "42") })
                Text("Vos transactions")
                List(success.account.transaction, id: \.self) { transaction in
                    NavigationLink(transaction, value: NavRoute.SecondScreen(userId: "2142"))
                        .fontWeight(.semibold)
                }
            }
        }
    }
}

#Preview("LOADING") {
    MyFirstView(mainScreenUIState: .Loading(),
                userId: "",
                events: .constant(nil))
}

#Preview("ERROR") {
    MyFirstView(mainScreenUIState: .Error(message: "An error"),
                userId: "",
                events: .constant(nil))
}

#Preview("FAILED") {
    MyFirstView(mainScreenUIState: .Success(profile: .init(username: "Joker"),
                                            account: .init(transaction: ["Tr1", "Tr2"])),
                userId: "",
                events: .constant(nil))
}
