package io.olezhabobrov

import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlinx.atomicfu.atomic

class ConcurrentSearchTree {

    private class TreeNode(val value: Int) {
        var leftChild = atomic<TreeNode?>(null)
        var rightChild = atomic<TreeNode?>(null)
        val lock = ReentrantReadWriteLock()
    }

    private var rootTree = atomic<TreeNode?>(null)

    fun add(inputValue: Int) {
        val newNode = TreeNode(inputValue)

        var placeForNewNode = rootTree

        while (true) {
            while (placeForNewNode.value != null) {
                val currentNode = placeForNewNode.value!!
                if (inputValue < currentNode.value) {
                    placeForNewNode = currentNode.leftChild
                } else {
                    placeForNewNode = currentNode.rightChild
                }
            }

            if (placeForNewNode.compareAndSet(null, newNode)) {
                break;
            }
        }
    }

    fun get(inputValue: Int): Boolean {
        var currentNode = rootTree
        while (currentNode.value != null) {
            if (currentNode.value!!.value == inputValue) {
                return true
            }
            if (inputValue < currentNode.value!!.value) {
                currentNode = currentNode.value!!.leftChild
            } else {
                currentNode = currentNode.value!!.rightChild
            }
        }
        return false
    }
}