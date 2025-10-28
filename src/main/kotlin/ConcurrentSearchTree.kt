package io.olezhabobrov

import kotlinx.atomicfu.AtomicInt
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlinx.atomicfu.atomic

class ConcurrentSearchTree {

    private class TreeNode(val key: Int, val value: AtomicInt) {
        var leftChild = atomic<TreeNode?>(null)
        var rightChild = atomic<TreeNode?>(null)
        val lock = ReentrantReadWriteLock()
    }

    private var rootTree = atomic<TreeNode?>(null)

    fun add(inputKey: Int, inputValue: Int) {
        val newNode = TreeNode(inputKey, atomic(inputValue))

        var placeForNewNode = rootTree

        while (true) {
            while (placeForNewNode.value != null) {
                val currentNode = placeForNewNode.value!!
                if (inputKey == currentNode.key) {
                    currentNode.value.getAndSet(inputValue)
                    return
                }

                if (inputKey < currentNode.key) {
                    placeForNewNode = currentNode.leftChild
                } else {
                    placeForNewNode = currentNode.rightChild
                }
            }

            if (placeForNewNode.compareAndSet(null, newNode)) {
                return
            }
        }
    }

    fun get(inputKey: Int): Int? {
        var currentAtomicNode = rootTree
        while (currentAtomicNode.value != null) {
            val currentNode = currentAtomicNode.value!!
            if (currentNode.key == inputKey) {
                return currentNode.value.value
            }
            if (inputKey < currentNode.key) {
                currentAtomicNode = currentNode.leftChild
            } else {
                currentAtomicNode = currentNode.rightChild
            }
        }
        return null
    }
}