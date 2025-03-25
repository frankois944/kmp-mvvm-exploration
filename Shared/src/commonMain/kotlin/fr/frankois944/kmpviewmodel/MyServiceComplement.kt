package fr.frankois944.kmpviewmodel

public interface MyServiceComplement {
    public val myData: String
    public fun myMethod() : String
    public fun myCallBack(callback: (String) -> Unit)
}