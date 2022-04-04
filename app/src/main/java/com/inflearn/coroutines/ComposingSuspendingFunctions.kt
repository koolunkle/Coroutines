package com.inflearn.coroutines

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// Async to sequential (Sequential by default / The Dream Code on Android)
// async (Concurrent using async / Lazily started async)
// Structured concurrency (Async-style functions (strongly discouraged) / Structured concurrency with async)

fun main() {
//    Sequential by default
    runBlocking {
        val time = measureTimeMillis {
            val one = doSomethingUsefulOne()
            val two = doSomethingUsefulTwo()
            println("The answer is ${one + two}")
        }
        println("Completed in $time ms")

//        Concurrent using async (What if there are no dependencies between invocations)
        val timeConcurrent = measureTimeMillis {
            val one = async { doSomethingUsefulOne() }
//          val oneRes = one.await()
            val two = async { doSomethingUsefulTwo() }
            println("The answer is ${one.await() + two.await()}")
        }
        println("Completed in $timeConcurrent ms")

//        Lazily started async
        val timeLazily = measureTimeMillis {
            val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
            val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
//          one.start()
//          two.start()
            println("The answer is ${one.await() + two.await()}")
        }
        println("Completed in $timeLazily ms")

//        Async-style functions (Kotlin coroutines is strongly discouraged)
        /* try {
            val timeAsync = measureTimeMillis {
                val one = somethingUsefulOneAsync()
                val two = somethingUsefulTwoAsync()

                println("my exceptions")
                throw Exception("my exceptions")

                runBlocking {
                    println("The answer is ${one.await() + two.await()}")
                }
            }
            println("Completed in $timeAsync ms")
        } catch (e: Exception) {
        }
        runBlocking {
            delay(100000)
        } */

//        Structured concurrency with async (This way, if throws an exception, all the coroutines will be cancelled)
        try {
            val timeStructured = measureTimeMillis {
                println("The answer is ${concurrentSum()}")
            }
            kotlin.io.println("Completed in $timeStructured ms")
        } catch (e: Exception) {
        }

        runBlocking {
            delay(10000)
        }

//        Cancellation propagated coroutines hierarchy
        try {
            failedConcurrentSum()
        } catch (e: ArithmeticException) {
            println("Computation failed with ArithmeticException")
        }
    }
}

suspend fun failedConcurrentSum(): Int = coroutineScope {
    val one = async<Int> {
        try {
            delay(Long.MAX_VALUE)
            42
        } finally {
            println("First child was cancelled")
        }
    }
    val two = async<Int> {
        println("Second child throws an exception")
        throw ArithmeticException()
    }
    one.await() + two.await()
}

suspend fun concurrentSum(): Int = coroutineScope {
    val one = async { doSomethingUsefulOne() }
    val two = async { doSomethingUsefulTwo() }

    delay(10)
    kotlin.io.println("Exception")
    throw Exception()

    one.await() + two.await()
}

fun somethingUsefulOneAsync() = GlobalScope.async {
    println("start, somethingUsefulOneAsync")
    val res = doSomethingUsefulOne()
    println("end, somethingUsefulOneAsync")
    res
}

fun somethingUsefulTwoAsync() = GlobalScope.async {
    println("start, somethingUsefulTwoAsync")
    val res = doSomethingUsefulTwo()
    println("end, somethingUsefulTwoAsync")
    res
}

suspend fun doSomethingUsefulOne(): Int {
    kotlin.io.println("start, doSomethingUsefulOne")
    delay(3000L)
    kotlin.io.println("end, doSomethingUsefulOne")
    /* println("doSomethingUsefulOne")
    delay(1000L) */
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    kotlin.io.println("start, doSomethingUsefulTwo")
    delay(3000L)
    kotlin.io.println("end, doSomethingUsefulTwo")
    /* println("doSomethingUsefulTwo")
    delay(1000L) */
    return 29
}

