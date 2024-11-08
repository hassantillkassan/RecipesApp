package com.example.recipesapp.ui

import android.os.Bundle
import com.example.recipesapp.model.Recipe

interface OnNavigationListener {
    fun navigateToCategories()
    fun navigateToFavorites()
    fun navigateToRecipesList(bundle: Bundle)
    fun navigateToRecipe(recipe: Recipe)
}