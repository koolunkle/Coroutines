package com.inflearn.coroutines

import kotlinx.coroutines.*

class Activity {
    private val mainScope = CoroutineScope(Dispatchers.Default)

    fun destroy() {
        mainScope.cancel()
    }

    fun doSomething() {
        repeat(10) { i ->
            mainScope.launch {
                delay((i + 1) + 200L)
                println("Coroutine $i is done")
            }
        }
    }
}

fun main() =
//    Dispatchers and threads (The coroutine context includes a coroutine dispatcher)
    runBlocking<Unit> {
        launch {
            println("main runBlocking: " + "I'm working in thread ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Unconfined) {
            println("Unconfined: " + "I'm working in thread ${Thread.currentThread().name}")
        }
        launch(Dispatchers.Default) {
            println("Default: " + "I'm working in thread ${Thread.currentThread().name}")
        }
        /* launch(newSingleThreadContext("MyOwnThread")) {
            println("newSingleThreadContext: " + "I'm working in thread ${Thread.currentThread().name}")
        } */
        newSingleThreadContext("MyOwnThread").use {
            launch(it) {
                println("newSingleThreadContext: " + "I'm working in thread ${Thread.currentThread().name}")
            }
        }

//        Debugging coroutines and threads (-Dkotlinx.coroutines.debug JVM option)
        val a = async {
            log("I'm computing a piece of the answer")
            6
        }
        val b = async {
            log("I'm computing another piece of the answer")
            7
        }
        log("The answer is ${a.await() * b.await()}")

//        Jumping between threads (withContext(): to change the context of a coroutine)
        newSingleThreadContext("Ctx1").use { ctx1 ->
            newSingleThreadContext("Ctx2").use { ctx2 ->
                runBlocking(ctx1) {
                    log("Started in ctx1")
                    withContext(ctx2) {
                        log("Working in ctx2")
                    }
                    log("Back to ctx1")
                }
            }
        }

//        Job in the context (The coroutine's Job is part of its context)
        println("My job is ${coroutineContext[Job]}")
        launch {
            println("My job is ${coroutineContext[Job]}")
        }
        async {
            println("My job is ${coroutineContext[Job]}")
        }
        coroutineContext[Job]?.isActive ?: true

//        Children of a coroutine (GlobalScope is used to launch a coroutine, there is no parent for the job)
        val request = launch {
            GlobalScope.launch {
                println("job1: I run in GlobalScope and execute independently!")
                delay(1000)
                println("job1: I am not affected by cancellation of the request")
            }
            launch {
                delay(100)
                println("job2: I am a child of the request coroutine")
                delay(1000)
                println("job2: I will not execute this line if my parent request is cancelled")
            }
        }
        delay(500)
        request.cancel()
        delay(1000)
        println("main: Who has survived request cancellation?")

//        Parental responsibilities (A parent coroutine always waits for completion of all its children)
        val requestParental = launch {
            repeat(3) { i ->
                launch {
                    delay((i + 1) + 200L)
                    println("Coroutine $i is done")
                }
            }
            println("request: I'm done and I don't explicitly join my children that are still active")
        }
//      requestParental.join()
        println("Now processing of the request is complete")

//        Combining context elements (Sometimes we need to define multiple elements for a coroutine context)
        launch(Dispatchers.Default + CoroutineName("test")) {
            println("I'm working in thread ${Thread.currentThread().name}")
        }

//        Coroutine scope
        val activity = Activity()
        activity.doSomething()
        println("Launched coroutines")
        delay(500L)
        println("Destroying activity!")
        activity.destroy()
        delay(3000)
    }

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")