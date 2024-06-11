import SwiftSyntax
import SwiftSyntaxBuilder
import SwiftSyntaxMacros
import SwiftSyntaxMacrosTestSupport
import XCTest

// Macro implementations build for the host, so the corresponding module is not available when cross-compiling. Cross-compiled tests may still make use of the macro itself in end-to-end tests.
#if canImport(KTViewModelBuilderMacros)
import KTViewModelBuilderMacros

let testMacros: [String: Macro.Type] = [
    "sharedViewModel": SharedViewModelMacro.self,
]
#endif

final class KTViewModelBuilderTests: XCTestCase {
    func testMacro() throws {
        #if canImport(KTViewModelBuilderMacros)
        assertMacroExpansion(
            """
            @sharedViewModel(ofType: MainScreenViewModel.self,
                             publishing: (\\.mainScreenUIState, MainScreenUIState.self), (\\.userId, String?.self)
            )
            class MainScreenVM: ObservableObject {}
            """,
            expandedSource: """
            class MainScreenVM: ObservableObject {

                private let viewModelStore = ViewModelStore()

                @Published private (set) var mainScreenUIState: MainScreenUIState

                @Published private (set) var userId: String?

                init(_ viewModel: MainScreenViewModel) {
                    self.viewModelStore.put(key: "MainScreenViewModelKey", viewModel: viewModel)
                    self.mainScreenUIState = viewModel.mainScreenUIState.value
                    print("INIT mainScreenUIState : " + String(describing: viewModel.mainScreenUIState.value))
                    self.userId = viewModel.userId.value
                    print("INIT userId : " + String(describing: viewModel.userId.value))
                }

                var instance: MainScreenViewModel {
                    self.viewModelStore.get(key: "MainScreenViewModelKey") as! MainScreenViewModel
                }

                func start() async {
                    await withTaskGroup(of: (Void).self) {
                        $0.addTask { @MainActor [weak self] in
                            for await value in self!.instance.mainScreenUIState where self != nil {
                                if value != self?.mainScreenUIState {
                                    #if DEBUG
                                    print("UPDATING mainScreenUIState : " + String(describing: value))
                                    #endif
                                    self?.mainScreenUIState = value
                                }
                            }
                        }
                        $0.addTask { @MainActor [weak self] in
                            for await value in self!.instance.userId where self != nil {
                                if value != self?.userId {
                                    #if DEBUG
                                    print("UPDATING userId : " + String(describing: value))
                                    #endif
                                    self?.userId = value
                                }
                            }
                        }
                    }
                }

                deinit {
                    self.viewModelStore.clear()
                }}
            """,
            macros: testMacros
        )
        #else
        throw XCTSkip("macros are only supported when running tests for the host platform")
        #endif
    }
}
