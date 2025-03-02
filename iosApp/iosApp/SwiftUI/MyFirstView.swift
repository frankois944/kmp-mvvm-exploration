//
//  MyFirstScreen.swift
//  iosApp
//
//  Created by François Dabonot on 24/04/2024.
//  Copyright © 2024 frankois944. All rights reserved.
//

import SwiftUI
import KTViewModelBuilder
import Combine
import Shared

struct MyFirstView: View {
    let mainScreenUIState: MainScreenUIState
    let userId: String?
    @Binding var events: MyFirstScreenUiEvents?
    @State private var selection: String?

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
                Button("RANDOM", action: {
                    events = .UpdateUserId(value: "42")
                })
                Text("Vos transactions")
                    /* List(success.account.transaction,
                     id: \.self,
                     selection: $selection) {
                     Text($0)
                     .fontWeight(.semibold)
                     }*/
                    .onChange(of: selection, perform: {
                        if $0 != nil {
                            events = .NextView()
                        }
                    })
                    .onDisappear(perform: {
                        selection = nil
                    })
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

#Preview("DONE") {
    MyFirstView(mainScreenUIState: .Success(profile: .init(username: "Joker")),
                userId: "",
                events: .constant(nil))
}
