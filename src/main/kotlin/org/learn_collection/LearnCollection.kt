package org.learn_collection

fun learnCollection() {
    val m = mutableMapOf<String, String>()
    m.put("foo", "bar")
    println(m)

    val m2 = listOf("a", "b", "c", "d")
    val m3 = listOf("x", "y", "z", "w")
    val m4 = m2.map { it.chars().toArray()[0] }.zip(m3)
    print(m4)

    // Iterator
    val numbers = listOf(1, 2, 3, 4)
    val iterator = numbers.listIterator()
    while (iterator.hasNext()) {
        // Iterator go to next position.
        println(iterator.next())
    }

    val numbers2 = mutableListOf(1, 2, 3, 4)
    val iterator2 = numbers2.listIterator()
    // In the beginning, iterator2 is ahead of the first element.
    // use next() can get the first element.
    val firstElement = iterator2.next()
    println("first element of numbers2: $firstElement")
    while (iterator2.hasNext()) {
        // If value of itrator2 equals 3, add 100 after iterator:
        // [1, 2, 3, 4] => [1, 2, 3, 100, 4]
        if (iterator2.next() == 3) {
            iterator2.add(100)
        }
    }
    println("numbers2: $numbers2")


    // Some complex operations

    // Associate: associateBy and associateWith can build Map from List.
    val n1 = listOf("one", "two", "three", "four")
    val n2 = n1.associateBy { it.length }
    // Length will be key and element with be value.
    // {3=two, 5=three, 4=four}
    println(n2)

    val n3 = listOf("www", "xxx", "yyy", "zzz")
    val n4 = n3.associateWith { it.length }
    // Element will be value and length will be key.
    // {www=3, xxx=3, yyy=3, zzz=3}
    println(n4)

    // Partition can separate elements into two groups with specified predicate condition:
    // Elements in the first group match the predicate.
    // Elements in the second group do not match the predicate.
    val (match, rest) = numbers2.partition { it < 10 }
    println("$match < 10, $rest >= 10")

    // + and -.
    // Plus (+) and minus (-) operators can modify collections.
    val plusList = numbers2 + 50
    // It is ok to have element not exists in numbers2.
    val minusList = numbers2 - listOf(2, 4, 99)
    println("plusList: $plusList, minusList: $minusList")

    val n6 = listOf("one", "two", "three", "four", "five", "six")
    // ["one", "two", "three", "four"]
    val slice61 = n6.slice(0..3)
    // ["two", "three"]
    val slice62 = n6.slice(1..<3)
    // ["three", "four", "five", "six"]
    val slice63 = n6.drop(2)
    // ["one", "two", "three"]
    val slice64 = n6.dropLast(3)
    // ["one", "two"]
    val slice65 = n6.take(2)
    // ["five", "six"]
    val slice66 = n6.takeLast(2)
    println(slice61)
    println(slice62)
    println(slice63)
    println(slice64)
    println(slice65)
    println(slice66)
}

