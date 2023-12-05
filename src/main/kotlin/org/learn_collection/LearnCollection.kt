package org.learn_collection

fun learnCollection() {
    val m = mutableMapOf<String, String>()
    m.put("foo", "bar")
    println(m)

    val m2 = listOf("a", "b", "c", "d")
    val m3 = listOf("x", "y", "z", "w")
    val m4 = m2.map { it.chars().toArray()[0] }.zip(m3)
    print(m4)
}

