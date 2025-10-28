package io.olezhabobrov

import java.util.concurrent.Executors
import kotlin.random.Random

fun main() {
    val tree = ConcurrentSearchTree()
    val threadCount = 10
    val insertsPerThread = 10000

    val executor = Executors.newFixedThreadPool(threadCount)

    repeat(threadCount) { threadIndex ->
        executor.submit {
            val rnd = Random(threadIndex)
            repeat(insertsPerThread) {
                val value = rnd.nextInt(-100, 101)
                tree.add(value)
            }
            println("Thread $threadIndex done")
        }
    }

    executor.shutdown()
    while (!executor.isTerminated) {
        Thread.sleep(10)
    }

    println("All threads finished.")
}