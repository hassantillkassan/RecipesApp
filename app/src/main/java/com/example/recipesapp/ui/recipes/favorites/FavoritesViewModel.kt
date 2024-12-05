package com.example.recipesapp.ui.recipes.favorites

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.ThreadPoolProvider
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.Recipe

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository()

    private val executor = ThreadPoolProvider.threadPool

    private val _state = MutableLiveData(FavoritesState())
    val state: LiveData<FavoritesState>
        get() = _state

    data class FavoritesState(
        val recipes: List<Recipe> = emptyList(),
        val isEmpty: Boolean = true,
        val errorMessage: String? = null,
    )

    fun loadFavorites() {
        executor.execute{
            try {
                val favoritesIds = getFavorites()
                val favoriteRecipes = recipesRepository.getRecipesByIds(favoritesIds)

                if (favoriteRecipes != null) {
                    _state.postValue(
                        _state.value?.copy(
                            recipes = favoriteRecipes,
                            isEmpty = favoriteRecipes.isEmpty(),
                        )
                    )
                } else {
                    _state.postValue(
                        _state.value?.copy(
                            errorMessage = "Ошибка получения данных",
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Ошибка при загрузке избранных рецептов")
            }
        }
    }

    private fun getFavorites(): Set<Int> {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("favorite_recipes_prefs", Context.MODE_PRIVATE)
        val favoriteIdsStringSet = sharedPreferences.getStringSet("favorites_recipes", emptySet()) ?: emptySet()

        return favoriteIdsStringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }
}