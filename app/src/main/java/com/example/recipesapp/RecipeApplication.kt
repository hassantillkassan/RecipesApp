package com.example.recipesapp

import android.app.Application
import com.example.recipesapp.di.AppContainer

class RecipeApplication: Application() {

    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(this)
    }
}