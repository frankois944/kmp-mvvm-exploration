import SwiftCompilerPlugin
import SwiftSyntax
import SwiftSyntaxBuilder
import SwiftSyntaxMacros
import SwiftUI

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
            item.as(InheritedTypeSyntax.self)?.type.as(IdentifierTypeSyntax.self)?.name.as(TokenSyntax.self)?.text != nil
        }) != nil else {
            fatalError("The Macro SharedViewModel can't only be used on a class with the protocol ObservableObject")
        }
        
        guard let attributes = functionDecl.attributes.first?.as(AttributeSyntax.self),
              let arguments = attributes.arguments?.as(LabeledExprListSyntax.self)  else {
            fatalError("The Macro SharedViewModel must have properties")
        }
        
        guard let viewModelType = arguments.first(where: { expr in
            expr.label?.text == "ofType"
        }) else {
            fatalError("The Macro SharedViewModel don't have type")
        }
        
        guard let className = viewModelType.expression.as(MemberAccessExprSyntax.self)?
            .base?.as(DeclReferenceExprSyntax.self)?
            .baseName.as(TokenSyntax.self)?
            .text else {
            fatalError("The Macro SharedViewModel don't have type")
        }
        
        var bindingList = [(binding: (name: String, type: String), isOptional: Bool)]()
        
        for (index, value) in arguments.enumerated() {
            if index == 0 {
                continue
            }
            let name = value.expression.as(TupleExprSyntax.self)?.elements.first?.as(LabeledExprSyntax.self)?.expression.as(KeyPathExprSyntax.self)?.components.first?.as(KeyPathComponentSyntax.self)?.component.as(KeyPathPropertyComponentSyntax.self)?.declName.baseName.text
            var type = value.expression.as(TupleExprSyntax.self)?.elements.dropFirst().first?.as(LabeledExprSyntax.self)?.expression.as(MemberAccessExprSyntax.self)?.base?.as(DeclReferenceExprSyntax.self)?.baseName.text
            var isOptional = false
            if type == nil {
                type = value.expression.as(TupleExprSyntax.self)?.elements.dropFirst().first?.as(LabeledExprSyntax.self)?.expression.as(MemberAccessExprSyntax.self)?.base?.as(OptionalChainingExprSyntax.self)?.expression.as(DeclReferenceExprSyntax.self)?.baseName.text
                isOptional = true
            }
            guard let name = name, let type = type else {
                fatalError("Invalid publishing couple \(String(describing: name)) \(String(describing: type))")
            }
            bindingList.append(((name, type), isOptional))
        }
        
    
        let viewModelStore = DeclSyntax(stringLiteral: "private let viewModelStore = ViewModelStore()")
        let disposeBag = DeclSyntax(stringLiteral: "private var jobs = Set<AnyCancellable>()")
        
        var bindings = [DeclSyntax]()
        bindingList.forEach { item in
            bindings.append(DeclSyntax(stringLiteral: "@Published private(set) var \(item.binding.name): \(item.binding.type)\(item.isOptional ? "?" : "" )"))
        }
        
        let initFunc = try InitializerDeclSyntax(SyntaxNodeString(stringLiteral: "init(_ viewModel: \(className))")) {
            StmtSyntax(stringLiteral: """

            self.viewModelStore.put(key: "\(className)Key", viewModel: viewModel)
            
            """)
            
            for item in bindingList {
                StmtSyntax(stringLiteral: """
                
                self.\(item.binding.name) = viewModel.\(item.binding.name).value
                print("INIT \(item.binding.name) : " + String(describing: viewModel.\(item.binding.name).value))
                jobs.insert(viewModel.\(item.binding.name).toPublisher()
                    .receive(on: DispatchQueue.main)
                    .sink { [weak self] in
                        if $0 != self?.\(item.binding.name) {
                            print("SINK \(item.binding.name) : " + String(describing: $0))
                            self?.\(item.binding.name) = $0
                        }
                    })
                
                """
                )
            }
        }
        
        let instanceAttr = DeclSyntax(stringLiteral: "var instance: \(className) { self.viewModelStore.get(key: \"\(className)Key\") as! \(className) }")
        let deinitFunc = DeinitializerDeclSyntax() {
            StmtSyntax(stringLiteral:"""
    jobs.forEach {
        $0.cancel()
    }
    jobs.removeAll()
    self.viewModelStore.clear()
""")
        }
        
        var result = [
            DeclSyntax(viewModelStore),
            DeclSyntax(disposeBag)
        ]
        result.append(contentsOf: bindings)
        result.append(contentsOf: [
            DeclSyntax(initFunc),
            DeclSyntax(instanceAttr),
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
