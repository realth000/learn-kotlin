package org.learn_function

// Default value
fun read(bytes: ByteArray, off: Int = 0, size: Int = bytes.size) {}

fun foo1(bar: Int, baz: Int) {}

fun foo2(bar: Int = 0, baz: Int) {}

fun foo3(bar: Int, baz: Int = 0, qux: Int) {}

fun foo4(bar: Int = 0, baz: Int, qux: () -> Unit) {}

fun foo5(bar: Int = 0, baz: () -> Unit, qux: Int) {}

fun foo6(bar: Int = 0, baz: Int = 0, qux: Int = 0) {}

fun foo7(vararg strings: String) {}

fun foo8(bar: Int) = bar * 2

infix fun Int.foo9(x: Int): Int = this + x

fun learnFunction() {
    //
    foo1(1, 2)

    // Fill all args
    foo2(1, 2)
    // Use bar default value, named value for baz
    foo2(baz = 1)
    // Use unnamed bar and named baz
    foo2(1, baz = 2)
    // Use named bar and unnamed baz
    foo2(bar = 1, 2)

    foo3(bar = 1, 2, 3)
    foo3(1, baz = 2, 3)
    // Unnamed args after positional (or call it "have default value") args
    // is not allowed.
    //foo3(baz = 2, 3)

    foo4(1, 2, { println('3') })
    // Or move the last lambda arg out of arg brackets.
    foo4(1, 2) { println('3') }
    // Named lambda arg
    foo4(1, 2, qux = { println('3') })

    foo5(1, { println('3') }, 2)
    foo5(1, baz = { println('3') }, 2)
    // baz can not move ouf of () because it is not the last arg
    // foo5(1, 2) { println('3') }

    // foo6 all args have default value, so we can sort them in any order when call it.
    foo6(baz = 2, bar = 1, qux = 3)
    // It's also ok to ignore some values in this case, those args will use default value.
    foo6(qux = 3, baz = 2)

    // `vararg` keyword in foo7
    foo7(strings = *arrayOf("a", "b", "c"))

    // foo8 is a single line function that its body is an expression.
    foo8(1)

    // foo9 has `infix` keyword, call this:
    2 foo9 3
    // equals to this:
    2.foo9(3)

    fun foo10() {}
    // foo10 is a function defined inside function, it is only visible in this function.

    // foo11 use `tailrec` keyword to make it a tail recursive function.
    // It MUST call itself in the last line of function body.
    // NOTE: Can not use in `open` function or `try`/`catch`/`final` block.
    tailrec fun foo11(x: Int): Int = if (x > 5) x else foo11(x + 1)
    println("foo11: ${foo11(3)}")

    //
}
