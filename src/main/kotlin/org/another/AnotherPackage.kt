package org.another

fun callAnotherPackage() {
    println("call another package")
    canNotCallThis()
    println("but you can use it by another public function")
}

private fun canNotCallThis() {
    println("you can not call this because this function is private")
}
