package org.learn_class

class LearnClass {
    // Properties can be init later
    lateinit var anotherName: String

    lateinit var programmer: Programmer

    fun person() {
        val person = Person("alice")
    }

    fun programmer() {
        programmer = Programmer("bob", "carol")
        // Check if person property initialized.
        println("is anotherName initialized? ${this::anotherName.isInitialized}")
        anotherName = "init another name".also { println("init another name") }
        println("is anotherName initialized? ${this::anotherName.isInitialized}")

        val busDriver = BusDriver("Erik", programmer)
        busDriver.takePassenger()
    }
}

/// Empty class
class Empty

/// Class [person] has a main constructor.
// Only classes have "open" keyword can be derived.
open class Person(name: String) {
    // This property will init first.
    val firstProperty = "First property: $name".also { value -> println(value) }

    // After `firstProperty` init finished, run this init function.
    init {
        println("run init")
        Hello().sayHello()
    }

    // Last the secondProperty will init.
    val secondProperty = "Second property: ${name.length}".also { value -> println(value) }

    // Can use parameters of main constructor in property init.
    val uppercaseName = name.uppercase()

    open val size: Int = firstProperty.length.also { println("init size in person") }

    // Nested class
    inner class Hello {
        fun sayHello() {
            println("Hello I'm ${firstProperty} from hello")
        }
    }
}

class Programmer(val name: String, private val nickName: String) : Person(name) {

    init {
        sayHello()
    }

    override val size: Int = (super.size + nickName.length).also { println("init size in programmer") }

    fun sayHello() {
        println("Hi, I am a programmer, $name")
    }
}

class BusDriver(val name: String, val programmer: Programmer) {
    fun takePassenger() {
        // This name is BusDriver's name
        programmer.askOnBoard(name)
    }

    // This extension on Programmer class is declared as a member of BusDriver.
    fun Programmer.askOnBoard(driverName: String) {
        // This field is in Programmer class, name is Programmer's name
        println("Hello programmer $name, I am bus driver $driverName, on board?")
    }
}

/// Extension on Programmer class
fun LearnClass.callProgrammer(myName: String) {
    println("hello ${this.programmer.name}, I'm $myName, nice to meet you")
}

data class User(val name: String, val age: Int)