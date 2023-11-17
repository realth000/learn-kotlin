package org.learn_generics

open class Base {
    override fun toString(): String {
        return "I'm Base"
    }
}

// Derived can do anything `Base` can.
class Derived : Base() {
    override fun toString(): String {
        return "I'm Derived"
    }
}

fun learnGenerics() {
    val baseList = ArrayList<Base>()
    writeAsBaseList(baseList)
    println(baseList[0])
    val derivedList = ArrayList<Derived>()
    derivedList.add(Derived())
    readAsBaseList(derivedList)
}

// The `in` keyword promises I will only write to `myList`,
// and it's safe to write a `Derived` into a list of `Base` because
// you can only use `myList`'s element as `Base`.
//
// Because `Derived` can do anything `Base` can, I won't worry about
// not finish its job.
fun writeAsBaseList(myList: ArrayList<in Derived>) {
    val myDerived = Derived()
    myList.add(myDerived)
}

// The `out` keyword promises I will only read elements in `myList`
// as `Base` type, so you can give me a list of `Derived`.
//
// Because `Derived` can do anything `Base` can, I won't worry about
// elements in `myList` can not finish its job.
fun readAsBaseList(myList: ArrayList<out Base>) {
    println(myList[0])
}



