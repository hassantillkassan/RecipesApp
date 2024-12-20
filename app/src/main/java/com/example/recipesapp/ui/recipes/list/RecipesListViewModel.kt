package com.example.recipesapp.ui.recipes.list

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.common.Constants
import com.example.recipesapp.data.AppDatabase
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.ErrorType
import com.example.recipesapp.model.Recipe
import kotlinx.coroutines.launch

class RecipesListViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)

    private val recipesRepository = RecipesRepository(database = database)

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
        viewModelScope.launch {
            _state.value = _state.value?.copy(error = null)

            val cachedRecipes = recipesRepository.getAllCachedRecipes()
            val filteredRecipes = cachedRecipes.filter { it.categoryId == category.id }

            if (filteredRecipes.isEmpty()) {
                val networkRecipes = recipesRepository.getRecipesByCategoryId(category.id)

                if (networkRecipes != null) {
                    val networkRecipesWithCategory = networkRecipes.map { recipe ->
                        recipe.copy(
                            categoryId = category.id,
                            imageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + recipe.imageUrl,
                        )
                    }

                    recipesRepository.saveRecipesToCache(networkRecipesWithCategory)

                    _state.value = _state.value?.copy(
                        recipes = networkRecipesWithCategory,
                        categoryName = category.title,
                        categoryImage = category.imageUrl,
                        error = null,
                    )
                } else {
                    _state.value = _state.value?.copy(
                        error = ErrorType.DATA_FETCH_ERROR,
                    )
                }
            } else {
               _state.value = _state.value?.copy(
                    recipes = filteredRecipes,
                    categoryName = category.title,
                    categoryImage = category.imageUrl,
                    error = null,
                )
            }

            /*_state.value = _state.value?.copy(error = null)

            val cachedRecipes = recipesRepository.getAllCachedRecipes()
            val filteredRecipes = cachedRecipes.filter { it.categoryId == category.id }

            if (filteredRecipes.isEmpty()) {
                val networkRecipes = recipesRepository.getRecipesByCategoryId(category.id)

                if (networkRecipes != null) {
                    recipesRepository.saveRecipesToCache(networkRecipes)

                    _state.value = _state.value?.copy(
                        recipes = networkRecipes.map { recipe ->
                            recipe.copy(imageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + recipe.imageUrl)
                        },
                        categoryName = category.title,
                        categoryImage = category.imageUrl,
                        error = null,
                    )
                } else {
                    _state.value = _state.value?.copy(
                        error = ErrorType.DATA_FETCH_ERROR,
                    )
                }
            } else {
                val updatedCachedRecipes = filteredRecipes.map { recipe ->
                    recipe.copy(imageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + recipe.imageUrl)
                }

                _state.value = _state.value?.copy(
                    recipes = updatedCachedRecipes,
                    categoryName = category.title,
                    categoryImage = category.imageUrl,
                    error = null,
                )
            }*/
        }
    }

    fun clearError() {
        _state.value = _state.value?.copy(error = null)
    }
}