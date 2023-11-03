fun myFunction(a: Int, b: Int): Int {
    return a + b
}

fun myFunction2(a: Int, b: Int) = a * b

/// foo is not a member.
/// bar is a member of MyClass.
/// bob is a private member.
class MyClass(foo: Double, var bar: String, private var bob: Int) {
    var baz = "foo:$foo, bar:$bar"
}

fun describe(obj: Any): String = when (obj) {
    1 -> "1!"
    5 -> "5!"
    "hello" -> "Brother here rap for you"
    is Long -> "This is a Long type val/var"
    !is String -> "Not a String"
    else -> "Unknown"
}

fun main(args: Array<String>) {
    // Function and class
    println("Hello World!")
    println("${myFunction(1, 2)}")
    println("${myFunction2(2, 3)}")
    val myClass = MyClass(10.0, "232", bob = 12)
    // Can not print private member "bob" here.
    println("${myClass.bar}, ${myClass.baz}")

    // Loop
    val items = listOf("apple", "banana", "pear")
    for (item in items) {
        println(item)
    }

    // "when" expression
    println("${describe(1)}, ${describe("hello")}, ${describe(3.3)}, ${describe(false)}")

    // Lambda filtering and mapping
    val items2 = listOf("apple", "banana", "pear")
    items2.filter { it.length > 4 }.map { it.uppercase() }.fold("") { acc, x -> "$acc $x" }.forEach { println(it) }
}