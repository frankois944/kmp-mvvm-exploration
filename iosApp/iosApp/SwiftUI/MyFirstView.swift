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
    let fruits: [FruitData]
    @Binding var events: MyFirstScreenUiEvents?
    @State private var selection: FruitData?

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
                Button("ADD RANDOME FRUIT", action: {
                    events = .AddNewFruit()
                })
                Button("REMOVE ALL FRUITS", action: {
                    events = .RemoveAllFruit()
                })
                Text("Vos transactions")
                List(fruits,
                     id: \.self,
                     selection: $selection) {
                    Text($0.fullName)
                        .fontWeight(.semibold)
                }.onChange(of: selection, perform: {
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
                fruits: [],
                events: .constant(nil))
}

#Preview("ERROR") {
    MyFirstView(mainScreenUIState: .Error(message: "An error"),
                userId: "",
                fruits: [],
                events: .constant(nil))
}

#Preview("DONE") {
    MyFirstView(mainScreenUIState: .Success(profile: .init(username: "Joker")),
                userId: "",
                fruits: [],
                events: .constant(nil))
}
