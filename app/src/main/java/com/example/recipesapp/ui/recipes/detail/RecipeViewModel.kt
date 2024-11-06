package com.example.recipesapp.ui.recipes.detail

import androidx.lifecycle.ViewModel
import com.example.recipesapp.R
import com.example.recipesapp.model.Ingredient

class RecipeViewModel : ViewModel() {

    data class RecipeState(
        val id: Int = 0,
        val title: String = "",
        val ingredients: List<Ingredient> = emptyList(),
        val method: List<String> = emptyList(),
        val imageUrl: Int = R.drawable.burger,
    )

}