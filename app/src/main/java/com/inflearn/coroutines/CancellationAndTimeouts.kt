package com.inflearn.coroutines

import kotlinx.coroutines.*

// Job (cancel())
// Cancellation is cooperative (to periodically invoke a suspending / explicitly check cancellation status(isActive))
// Timeout (withTimeout / withTimeoutOrNull)

fun main() {
//    Cancelling coroutine execution
    runBlocking {
        val job = launch {
            repeat(1000) { i ->
                println("job: I'm sleeping $i ...")
                delay(500L)
            }
        }
        delay(1300L)
        println("main: I'm tired of waiting!")
        job.cancel()
        job.join()
        println("main: Now I can quit.")
    }

//    Cancellation is cooperative
    runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            try {
                var nextPrintTime = startTime
                var i = 0
                while (i < 5) {
                    if (System.currentTimeMillis() >= nextPrintTime) {
//                    delay(1L)
//                    yield()
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            } catch (e: Exception) {
                kotlin.io.println("Exception [$e]")
            }
        }
        delay(1300L)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

//    Making computation code cancellable
    runBlocking {
        val startTime = System.currentTimeMillis()
        val job = launch(Dispatchers.Default) {
            try {
                var nextPrintTime = startTime
                var i = 0
                kotlin.io.println("isActive $isActive ...")
                while (isActive) {
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
                kotlin.io.println("isActive $isActive ...")
            } catch (e: Exception) {
                kotlin.io.println("Exception [$e] ...")
            }
        }
        delay(1300L)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

//    Closing resources with finally
    runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    com.inflearn.coroutines.println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                println("job: I'm running finally")
            }
        }
        delay(1300L)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

//    Run non-cancellable bloc
    runBlocking {
        val job = launch {
            try {
                repeat(1000) { i ->
                    com.inflearn.coroutines.println("job: I'm sleeping $i ...")
                    delay(500L)
                }
            } finally {
                withContext(NonCancellable) {
                    println("job: I'm running finally")
                    delay(1000L)
                    println("job: And I've just delayed for 1 sec because I'm non-cancellable")
                }
            }
        }
        delay(1300L)
        println("main: I'm tired of waiting!")
        job.cancelAndJoin()
        println("main: Now I can quit.")
    }

//    Timeout
    /* runBlocking {
        withTimeout(1300L) {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
        }
    } */

//    withTimeoutOrNull
    runBlocking {
        val result = withTimeoutOrNull(1300L) {
            repeat(1000) { i ->
                println("I'm sleeping $i ...")
                delay(500L)
            }
            "Done"
        }
        println("Result is $result")
    }
}


