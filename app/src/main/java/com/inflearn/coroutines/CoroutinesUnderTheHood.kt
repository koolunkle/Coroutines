package com.inflearn.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

// There is no magic (CPS == Callbacks / CPS Transformation)
// Decompile (Labels / Callback)
// CPS simulation (debugging)

fun main(): Unit {
//    Like the dream code
    GlobalScope.launch {
        val userData = fetchUserData()
        val userCache = cacheUserData(userData)
        updateTExtView(userCache)
    }
}

suspend fun fetchUserData() = "user_name"

suspend fun cacheUserData(user: String) = user

fun updateTExtView(user: String) = user