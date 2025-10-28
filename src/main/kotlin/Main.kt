package io.olezhabobrov

fun main() {
    val tree = ConcurrentSearchTree()

    tree.add(1)
    tree.add(2)
    tree.add(-1)
    tree.add(3)

    println(tree.get(0))
    println(tree.get(1))
    println(tree.get(2))
    println(tree.get(3))
    println(tree.get(4))
}