package com.example.recipesapp

import android.os.Bundle

interface OnNavigationListener {
    fun navigateToCategories()
    fun navigateToFavorites()
    fun navigateToRecipes(bundle: Bundle)
}