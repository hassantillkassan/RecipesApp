package com.example.recipesapp.ui.recipes.detail

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.common.Constants
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.ErrorType
import com.example.recipesapp.model.Recipe
import kotlinx.coroutines.launch

class RecipeViewModel(application: Application) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository()

    private val _state = MutableLiveData(RecipeState())
    val state: LiveData<RecipeState>
        get() = _state

    data class RecipeState(
        val recipe: Recipe? = null,
        val portionCount: Int = 1,
        val isFavorite: Boolean = false,
        val recipeImage: String? = null,
        val error: ErrorType? = null,
    )

    fun loadRecipe(recipeId: Int) {
        viewModelScope.launch {
            try {
                val recipe = recipesRepository.getRecipeById(recipeId)

                val favorites = getFavorites()

                val recipeImageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + recipe?.imageUrl

                if (recipe != null) {
                    val updatedRecipe = recipe.copy(imageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + recipe.imageUrl)
                    val isFavorite = favorites.contains(recipeId.toString())

                    _state.postValue(
                        _state.value?.copy(
                            recipe = updatedRecipe,
                            isFavorite = isFavorite,
                            recipeImage = recipeImageUrl,
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
                Log.e("RecipeViewModel", "Ошибка при загрузке рецепта", e)
                _state.postValue(
                    _state.value?.copy(
                        error = ErrorType.UNKNOWN_ERROR,
                    )
                )
            }
        }
    }

    private fun getFavorites(): MutableSet<String> {
        val sharedPrefs =
            getApplication<Application>().getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        val favorites =
            sharedPrefs.getStringSet(Constants.FAVORITES_KEY, emptySet()) ?: emptySet()

        return HashSet(favorites)
    }

    private fun saveFavorites(favorites: Set<String>) {
        val sharedPrefs =
            getApplication<Application>().getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().putStringSet(Constants.FAVORITES_KEY, favorites).apply()
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

    fun updatePortionCount(portionCount: Int) {
        _state.value = _state.value?.copy(portionCount = portionCount)
    }

    fun clearError() {
        _state.postValue(_state.value?.copy(error = null))
    }

}