package com.example.recipesapp.di

import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.ui.recipes.favorites.FavoritesViewModel

class FavoritesViewModelFactory(
    private val recipesRepository: RecipesRepository,
) : Factory<FavoritesViewModel>{

    override fun create(): FavoritesViewModel {
        return FavoritesViewModel(recipesRepository)
    }
}