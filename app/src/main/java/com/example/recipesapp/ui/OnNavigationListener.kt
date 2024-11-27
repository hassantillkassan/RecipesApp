package com.example.recipesapp.ui

import android.os.Bundle
import androidx.navigation.NavDirections
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.Recipe

interface OnNavigationListener {
    fun navigateToCategories()
    fun navigateToFavorites()
    fun navigateToRecipesList(category: Category)
    fun navigateToRecipe(recipe: Recipe)
}