package com.example.recipesapp.ui.recipes.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.common.Constants
import com.example.recipesapp.common.ThreadPoolProvider
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.ErrorType
import com.example.recipesapp.model.Recipe

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository()

    private val _state = MutableLiveData(RecipesListState())
    val state: LiveData<RecipesListState>
        get() = _state

    data class RecipesListState(
        val recipes: List<Recipe> = emptyList(),
        val categoryName: String = "",
        val categoryImage: String? = null,
        val error: ErrorType? = null,
    )

    fun loadRecipe(category: Category) {
        ThreadPoolProvider.threadPool.execute {
            try {
                val recipes = recipesRepository.getRecipesByCategoryId(category.id)

                if (recipes != null) {
                    val updatedRecipes = recipes.map { recipe ->
                        recipe.copy(imageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + recipe.imageUrl)
                    }

                    _state.postValue(
                        _state.value?.copy(
                            recipes = updatedRecipes,
                            categoryName = category.title,
                            categoryImage = category.imageUrl,
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
                Log.e("RecipesListViewModel", "Ошибка при загрузке списка рецептов", e)
                _state.postValue(
                    _state.value?.copy(
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