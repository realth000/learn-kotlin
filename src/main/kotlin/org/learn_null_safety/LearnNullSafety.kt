package org.learn_null_safety

fun learnNullSafety() {
    var i1 = 2
    // i1 can not be null
    // i1 = null

    var i2: Int? = 2
    // i2 can be null because its nullable type
    i2 = null

    // Use "ii" to convert to non-nullable type.
    var i3: Int = i1

    // i4 will be null if i2 is null when casting
    val i4 = i2 as? Int

    // Use `filterNotNull` to filter not null elements.
    // Same `whereType` in dart.
    val i5: List<Int> = listOf(1, 2, null, 4).filterNotNull()

    // i6 will be 2 if i4 is null
    // Same `??` in dart.
    val i6 = i4 ?: 2
}
