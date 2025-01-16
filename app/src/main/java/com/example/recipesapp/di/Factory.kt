package com.example.recipesapp.di

interface Factory<T> {
    fun create(): T
}