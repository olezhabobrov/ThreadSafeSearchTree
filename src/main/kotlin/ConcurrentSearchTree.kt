package io.olezhabobrov

import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic

fun ByteArray.less(other: ByteArray): Boolean {
    val minLength = minOf(this.size, other.size)
    for (i in 0 until minLength) {
        val diff = (this[i].toInt() and 0xFF) - (other[i].toInt() and 0xFF)
        if (diff != 0) {
            return diff < 0
        }
    }
    return this.size < other.size
}

class ConcurrentSearchTree {

    // Key is never changed, but corresponding value can be.
    private class TreeNode(val key: ByteArray, val value: AtomicRef<ByteArray>) {
        var leftChild = atomic<TreeNode?>(null)
        var rightChild = atomic<TreeNode?>(null)
    }

    private var rootTree = atomic<TreeNode?>(null)

    fun add(inputKey: ByteArray, inputValue: ByteArray) {
        val newNode = TreeNode(inputKey, atomic(inputValue))

        var placeForNewNode = rootTree

        while (true) {
            while (placeForNewNode.value != null) {
                // When we add a node, it is there forever. It can't become null.
                val currentNode = placeForNewNode.value!!
                if (inputKey.contentEquals(currentNode.key)) {
                    currentNode.value.getAndSet(inputValue)
                    return
                }

                if (inputKey.less(currentNode.key)) {
                    placeForNewNode = currentNode.leftChild
                } else {
                    placeForNewNode = currentNode.rightChild
                }
            }

            // Check if we found a leaf. Another thread could have already added here a node.
            // It is safe to continue looking for a leaf in this subtree, as we don't rebalance a tree.
            if (placeForNewNode.compareAndSet(null, newNode)) {
                return
            }
        }
    }

    fun get(inputKey: ByteArray): ByteArray? {
        var currentAtomicNode = rootTree
        while (currentAtomicNode.value != null) {
            // When we add a node, it is there forever. It can't become null.
            val currentNode = currentAtomicNode.value!!
            if (currentNode.key.contentEquals(inputKey)) {
                return currentNode.value.value
            }
            if (inputKey.less(currentNode.key)) {
                currentAtomicNode = currentNode.leftChild
            } else {
                currentAtomicNode = currentNode.rightChild
            }
        }
        return null
    }
}