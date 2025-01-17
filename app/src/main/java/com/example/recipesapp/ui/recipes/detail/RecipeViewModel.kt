package com.example.recipesapp.ui.recipes.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.ErrorType
import com.example.recipesapp.model.Recipe
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeViewModel @Inject constructor(
    private val recipesRepository: RecipesRepository,
) : ViewModel() {

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
        Log.d("RecipeViewModel", "Загрузка рецепта: ID = $recipeId")
        viewModelScope.launch {
            try {
                val recipe = recipesRepository.getRecipeById(recipeId)
                Log.d("RecipeViewModel", "Рецепт загружен: ID = ${recipe?.id}, Избранное = ${recipe?.isFavorite}")

                if (recipe != null) {
                    _state.postValue(
                        _state.value?.copy(
                            recipe = recipe.copy(isFavorite = recipe.isFavorite),
                            isFavorite = recipe.isFavorite,
                            recipeImage = recipe.updatedImageUrl,
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

    fun onFavoritesClicked() {
        val recipeId = _state.value?.recipe?.id ?: return
        val isCurrentlyFavorite = state.value?.isFavorite ?: false

        viewModelScope.launch {
            recipesRepository.updateRecipeFavoriteStatus(recipeId, !isCurrentlyFavorite)
        }

        _state.value = _state.value?.copy(isFavorite = !isCurrentlyFavorite)
    }

    fun updatePortionCount(portionCount: Int) {
        _state.value = _state.value?.copy(portionCount = portionCount)
    }

    fun clearError() {
        _state.postValue(_state.value?.copy(error = null))
    }
}