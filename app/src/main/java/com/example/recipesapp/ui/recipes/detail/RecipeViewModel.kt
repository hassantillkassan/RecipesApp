package com.example.recipesapp.ui.recipes.detail

import androidx.lifecycle.ViewModel
import com.example.recipesapp.model.Recipe

class RecipeViewModel : ViewModel() {

    data class RecipeState(
        val recipe: Recipe? = null,
        val portionCount: Int = 1,
        val isFavorite: Boolean = false,
    )

}