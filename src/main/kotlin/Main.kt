package io.olezhabobrov

import java.util.concurrent.Executors
import kotlin.random.Random

fun main() {
    val tree = ConcurrentSearchTree()
    val threadCount = 100
    val insertsPerThread = 20

    val executor = Executors.newFixedThreadPool(threadCount)

    repeat(threadCount) { threadIndex ->
        executor.submit {
            val rnd = Random(threadIndex)
            repeat(insertsPerThread) {
                rnd.nextBytes(10)
                tree.add(rnd.nextBytes(10), rnd.nextBytes(10))
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