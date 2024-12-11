package com.example.recipesapp.ui.recipes.favorites

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.common.Constants
import com.example.recipesapp.common.ThreadPoolProvider
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.ErrorType
import com.example.recipesapp.model.Recipe

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository()

    private val _state = MutableLiveData(FavoritesState())
    val state: LiveData<FavoritesState>
        get() = _state

    data class FavoritesState(
        val recipes: List<Recipe> = emptyList(),
        val isEmpty: Boolean = true,
        val error: ErrorType? = null,
    )

    fun loadFavorites() {
        ThreadPoolProvider.threadPool.execute{
            try {
                val favoritesIds = getFavorites()
                val favoriteRecipes = recipesRepository.getRecipesByIds(favoritesIds)

                if (favoriteRecipes != null) {
                    val updatedFavoriteRecipe = favoriteRecipes.map { recipe ->
                        recipe.copy(imageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + recipe.imageUrl)
                    }

                    _state.postValue(
                        _state.value?.copy(
                            recipes = updatedFavoriteRecipe,
                            isEmpty = favoriteRecipes.isEmpty(),
                            error = null,
                        )
                    )
                } else {
                    _state.postValue(
                        _state.value?.copy(
                            error = ErrorType.DATA_FETCH_ERROR,
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Ошибка при загрузке избранных рецептов", e)
                _state.postValue(
                    _state.value?.copy(
                        error = ErrorType.UNKNOWN_ERROR,
                    )
                )
            }
        }
    }

    private fun getFavorites(): Set<Int> {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences("favorite_recipes_prefs", Context.MODE_PRIVATE)
        val favoriteIdsStringSet = sharedPreferences.getStringSet("favorites_recipes", emptySet()) ?: emptySet()

        return favoriteIdsStringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun clearError() {
        _state.postValue(_state.value?.copy(error = null))
    }
}