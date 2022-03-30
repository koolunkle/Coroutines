package com.inflearn.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

// Coroutine builder (launch / runBlocking)
// Scope (CoroutineScope / GlobalScope)
// Suspend function (suspend / delay() / join())
// Structured concurrency

fun main() {
//    Coroutines
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    Thread.sleep(2000L)

    thread {
        Thread.sleep(1000L)
        println("World!")
    }
    println("Hello,")
    Thread.sleep(2000L)

//    runBlocking (coroutines builder)
    GlobalScope.launch {
        delay(1000L)
        println("World!")
    }
    println("Hello,")
    runBlocking {
        delay(2000L)
    }

    runBlocking {
        GlobalScope.launch {
            delay(1000L)
            println("World!")
        }
        println("Hello,")
        delay(2000L)
    }

//    Waiting for a job (Delaying is not a good approach)
    runBlocking {
        val job = GlobalScope.launch {
            delay(1000L)
            println("World!")
        }
        println("Hello,")
        job.join()
    }

//    Structured concurrency
    runBlocking {
        launch {
            delay(1000L)
            println("World!")
        }
        println("Hello,")
    }

//    Extract function refactoring (Suspending function)
    runBlocking {
        launch {
            myWorld()
        }
        println("Hello,")
    }

//    Coroutines are light-weight
    runBlocking {
        repeat(100_000) {
            launch {
                delay(1000L)
                println(".")
            }
        }
    }

    runBlocking {
        repeat(100_000) {
            thread {
                Thread.sleep(1000L)
                println(".")
            }
        }
    }

//    Global coroutines are like daemon threads
    runBlocking {
        GlobalScope.launch {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        }
        delay(1300L)
    }

//    Global coroutines are like daemon threads (suspend-resume)
    runBlocking {
        launch {
            repeat(5) { i ->
                println("Coroutine A, $i")
//                delay(10L)
            }
        }
        launch {
            repeat(5) { i ->
                println("Coroutine B, $i")
//                delay(10L)
            }
        }
        println("Coroutine Outer")
    }
}

fun <T> println(msg: T) {
    kotlin.io.println("$msg [${Thread.currentThread().name}]")
}

suspend fun myWorld() {
    delay(1000L)
    println("World!")
}