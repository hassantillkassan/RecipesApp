package com.example.recipesapp.ui.recipes.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.ErrorType
import com.example.recipesapp.model.Recipe
import kotlinx.coroutines.launch

class RecipesListViewModel(
    private val recipesRepository: RecipesRepository,
) : ViewModel() {

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
        }
    }

    fun clearError() {
        _state.value = _state.value?.copy(error = null)
    }
}