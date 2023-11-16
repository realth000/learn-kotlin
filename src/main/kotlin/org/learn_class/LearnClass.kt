package org.example.org.learn_class

class LearnClass

/// Empty class
class Empty

/// Class [person] has a main constructor.
class Person(name: String) {
    val firstProperty = "First property: $name".also { println("first property init") }

    init {
        println()
    }

    val secondProperty = "Second property: ${name.length}".also { println("second property init") }
}
