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
                val key = rnd.nextInt(0, 1000)
//                val value = rnd.nextInt(-100, 101)
                tree.add(key, key)
            }
            println("Thread $threadIndex done")
        }
    }

    executor.shutdown()
    while (!executor.isTerminated) {
        Thread.sleep(10)
    }

    println("All threads finished.")

    for (i in 0..1000) {
        println(tree.get(i))
    }
}