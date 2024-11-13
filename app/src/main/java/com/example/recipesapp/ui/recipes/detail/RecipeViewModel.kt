package com.example.recipesapp.ui.recipes.detail

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.data.STUB
import com.example.recipesapp.model.Recipe

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableLiveData<RecipeState>().apply { value = RecipeState() }
    val state: LiveData<RecipeState>
        get() = _state

    data class RecipeState(
        val recipe: Recipe? = null,
        val portionCount: Int = 1,
        val isFavorite: Boolean = false,
    )

    fun loadRecipe(recipeId: Int) {
        // TODO: load from network

        val recipe = STUB.getRecipeById(recipeId)
        val favorites = getFavorites()

        _state.value = _state.value?.copy(
            recipe = recipe,
            isFavorite = favorites.contains(recipeId.toString()),
        )
    }

    private fun getFavorites(): MutableSet<String> {
        val sharedPrefs =
            getApplication<Application>().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val favorites =
            sharedPrefs.getStringSet(FAVORITES_KEY, emptySet()) ?: emptySet()

        return HashSet(favorites)
    }

    private fun saveFavorites(favorites: Set<String>) {
        val sharedPrefs =
            getApplication<Application>().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().putStringSet(FAVORITES_KEY, favorites).apply()
    }

    fun onFavoritesClicked() {
        val recipeId = _state.value?.recipe?.id ?: return
        val favorites = getFavorites().toMutableSet()
        val isCurrentlyFavorite = state.value?.isFavorite ?: false

        if (isCurrentlyFavorite) {
            favorites.remove(recipeId.toString())
        } else {
            favorites.add(recipeId.toString())
        }

        saveFavorites(favorites)
        _state.value = _state.value?.copy(isFavorite = !isCurrentlyFavorite)
    }

    companion object {
        private const val SHARED_PREFS_NAME = "favorite_recipes_prefs"
        private const val FAVORITES_KEY = "favorites_recipes"
    }

}