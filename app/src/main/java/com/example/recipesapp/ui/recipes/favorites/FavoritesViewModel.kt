package com.example.recipesapp.ui.recipes.favorites

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.data.STUB
import com.example.recipesapp.model.Recipe

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableLiveData(FavoritesState())
    val state: LiveData<FavoritesState>
        get() = _state

    data class FavoritesState(
        val recipes: List<Recipe> = emptyList(),
        val isEmpty: Boolean = true,
    )

    fun loadFavorites() {
        val favoritesIds = getFavorites()
        val favoriteRecipes = STUB.getRecipesByIds(favoritesIds)

        _state.value = _state.value?.copy(
            recipes = favoriteRecipes,
            isEmpty = favoriteRecipes.isEmpty(),
        )
    }

    private fun getFavorites(): Set<Int> {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("favorite_recipes_prefs", Context.MODE_PRIVATE)
        val favoriteIdsStringSet = sharedPreferences.getStringSet("favorites_recipes", emptySet()) ?: emptySet()

        return favoriteIdsStringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }
}