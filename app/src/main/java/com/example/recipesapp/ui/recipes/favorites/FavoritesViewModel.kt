package com.example.recipesapp.ui.recipes.favorites

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
        }
    }

    fun clearError() {
        _state.postValue(_state.value?.copy(error = null))
    }
}