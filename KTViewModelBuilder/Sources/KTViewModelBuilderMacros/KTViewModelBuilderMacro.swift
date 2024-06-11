import SwiftCompilerPlugin
import SwiftSyntax
import SwiftSyntaxBuilder
import SwiftSyntaxMacros

public struct SharedViewModelMacro: MemberMacro {
    
    public static func expansion(
        of node: AttributeSyntax,
        providingMembersOf declaration: some DeclGroupSyntax,
        in context: some MacroExpansionContext
    ) throws -> [SwiftSyntax.DeclSyntax] {
        guard let functionDecl = declaration.as(ClassDeclSyntax.self) else {
            fatalError("The Macro SharedViewModel can't only be apply on a class")
        }
        
        guard functionDecl.inheritanceClause?.inheritedTypes.first(where: { item in
            item.type.as(IdentifierTypeSyntax.self)?.name.text == "ObservableObject"
        }) != nil else {
            fatalError("The Macro SharedViewModel can't only be used on a class using the protocol ObservableObject")
        }
        
        guard let attributes = functionDecl.attributes.first?.as(AttributeSyntax.self),
              let arguments = attributes.arguments?.as(LabeledExprListSyntax.self)  else {
            fatalError("The Macro SharedViewModel must have properties")
        }
        
        guard let viewModelType = arguments.first(where: { expr in
            expr.label?.text == "ofType"
        }) else {
            fatalError("The Macro SharedViewModel don't have type (ofType)")
        }
        
        guard let className = viewModelType.expression.as(MemberAccessExprSyntax.self)?
            .base?.as(DeclReferenceExprSyntax.self)?
            .baseName.text else {
            fatalError("The Macro SharedViewModel don't have type")
        }
        
        var bindingList = [(binding: (name: String, type: String), isOptional: Bool)]()
        
        for (index, value) in arguments.enumerated() {
            if index == 0 {
                continue
            }
            let name = value.expression.as(TupleExprSyntax.self)?.elements.first?.expression
                .as(KeyPathExprSyntax.self)?.components.first?.component
                .as(KeyPathPropertyComponentSyntax.self)?.declName.baseName.text
            var type = value.expression
                .as(TupleExprSyntax.self)?.elements.dropFirst().first?.expression
                .as(MemberAccessExprSyntax.self)?.base?
                .as(DeclReferenceExprSyntax.self)?.baseName.text
            var isOptional = false
            if type == nil {
                type = value.expression
                    .as(TupleExprSyntax.self)?.elements.dropFirst().first?.expression
                    .as(MemberAccessExprSyntax.self)?.base?
                    .as(OptionalChainingExprSyntax.self)?.expression
                    .as(DeclReferenceExprSyntax.self)?.baseName.text
                isOptional = true
            }
            guard let name = name, let type = type else {
                fatalError("Invalid publishing couple \(String(describing: name)) \(String(describing: type))")
            }
            bindingList.append(((name, type), isOptional))
        }
        
    
        let viewModelStore = DeclSyntax(stringLiteral: "private let viewModelStore = ViewModelStore()")
        
        var bindings = [DeclSyntax]()
        bindingList.forEach { item in
            bindings.append(DeclSyntax(stringLiteral: "@Published private(set) var \(item.binding.name): \(item.binding.type)\(item.isOptional ? "?" : "" )"))
        }
        
        let initFunc = try InitializerDeclSyntax(SyntaxNodeString(stringLiteral: "init(_ viewModel: \(className))")) {
            ExprSyntax(stringLiteral: """
            self.viewModelStore.put(key: "\(className)Key", viewModel: viewModel)
            """)
            for item in bindingList {
                ExprSyntax(stringLiteral: """
                self.\(item.binding.name) = viewModel.\(item.binding.name).value
                """)
                ExprSyntax(stringLiteral: """
                print("INIT \(item.binding.name) : " + String(describing: viewModel.\(item.binding.name).value))
                """)
            }
        }
        
        let instanceAttr = DeclSyntax(stringLiteral: """
            var instance: \(className) { self.viewModelStore.get(key: \"\(className)Key\") as! \(className) }
            """)
        
        let startViewModel = try FunctionDeclSyntax("func start() async") {
            ExprSyntax(stringLiteral: """
            await withTaskGroup(of: (Void).self) { group in
            """)
            for item in bindingList {
                ExprSyntax(stringLiteral: """
                group.addTask { @MainActor [weak self] in
                    if self != nil {
                        for await value in self!.instance.\(item.binding.name) {
                            if value != self?.\(item.binding.name) {
                                self?.\(item.binding.name) = value
                            }
                        }
                    }
                }
                """)
            }
            ExprSyntax(stringLiteral: """
            }
            """)
        }
        
        let deinitFunc = DeinitializerDeclSyntax() {
            ExprSyntax(stringLiteral: """
            self.viewModelStore.clear()
            """)
        }
        
        var result = [
            DeclSyntax(viewModelStore),
        ]
        result.append(contentsOf: bindings)
        result.append(contentsOf: [
            DeclSyntax(initFunc),
            DeclSyntax(instanceAttr),
            DeclSyntax(startViewModel),
            DeclSyntax(deinitFunc)
        ])
        return result
    }
}



@main
struct KTViewModelBuilderPlugin: CompilerPlugin {
    let providingMacros: [Macro.Type] = [
        SharedViewModelMacro.self
    ]
}
