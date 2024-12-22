package com.example.recipesapp.ui.recipes.favorites

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.common.Constants
import com.example.recipesapp.data.AppDatabase
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.ErrorType
import com.example.recipesapp.model.Recipe
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)

    private val recipesRepository = RecipesRepository(database = database)

    private val _state = MutableLiveData(FavoritesState())
    val state: LiveData<FavoritesState>
        get() = _state

    data class FavoritesState(
        val recipes: List<Recipe> = emptyList(),
        val isEmpty: Boolean = true,
        val isLoading: Boolean = true,
        val error: ErrorType? = null,
    )

    fun loadFavorites() {
        _state.postValue(
            _state.value?.copy(
                isEmpty = false,
                isLoading = true,
                error = null,
            )
        )

        viewModelScope.launch {
            try {
                val favoriteRecipes = recipesRepository.getFavoriteRecipes()

                _state.postValue(
                    _state.value?.copy(
                        recipes = favoriteRecipes,
                        isEmpty = favoriteRecipes.isEmpty(),
                        isLoading = false,
                        error = null,
                    )
                )
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Ошибка при загрузке избранных рецептов", e)
                _state.postValue(
                    _state.value?.copy(
                        isEmpty = false,
                        isLoading = false,
                        error = ErrorType.UNKNOWN_ERROR,
                    )
                )
            }

            /*try {
                val favoritesIds = getFavorites()

                if (favoritesIds.isEmpty()) {
                    _state.postValue(
                        _state.value?.copy(
                            recipes = emptyList(),
                            isEmpty = true,
                            isLoading = false,
                            error = null,
                        )
                    )

                    return@launch
                }

                val favoriteRecipes = recipesRepository.getRecipesByIds(favoritesIds)

                if (favoriteRecipes != null) {
                    val updatedFavoriteRecipe = favoriteRecipes.map { recipe ->
                        recipe.copy(imageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + recipe.imageUrl)
                    }

                    _state.postValue(
                        _state.value?.copy(
                            recipes = updatedFavoriteRecipe,
                            isEmpty = favoriteRecipes.isEmpty(),
                            isLoading = false,
                            error = null,
                        )
                    )
                } else {
                    _state.postValue(
                        _state.value?.copy(
                            isEmpty = false,
                            isLoading = false,
                            error = ErrorType.DATA_FETCH_ERROR,
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Ошибка при загрузке избранных рецептов", e)
                _state.postValue(
                    _state.value?.copy(
                        isEmpty = false,
                        isLoading = false,
                        error = ErrorType.UNKNOWN_ERROR,
                    )
                )
            }*/
        }
    }

    private fun getFavorites(): Set<Int> {
        val sharedPreferences =
            getApplication<Application>().getSharedPreferences(
                Constants.SHARED_PREFS_NAME,
                Context.MODE_PRIVATE
            )
        val favoriteIdsStringSet =
            sharedPreferences.getStringSet(Constants.FAVORITES_KEY, emptySet()) ?: emptySet()

        return favoriteIdsStringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun clearError() {
        _state.postValue(_state.value?.copy(error = null))
    }
}