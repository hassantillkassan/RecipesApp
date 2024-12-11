package com.example.recipesapp.common

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object ThreadPoolProvider {
    val threadPool: ExecutorService = Executors.newFixedThreadPool(10)
}