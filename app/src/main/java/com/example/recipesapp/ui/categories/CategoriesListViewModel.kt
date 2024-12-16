package com.example.recipesapp.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.common.Constants
import com.example.recipesapp.data.AppDatabase
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.ErrorType
import kotlinx.coroutines.launch

class CategoriesListViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)

    private val recipesRepository = RecipesRepository(database = database)

    private val _state = MutableLiveData(CategoriesListState())
    val state: LiveData<CategoriesListState>
        get() = _state

    data class CategoriesListState(
        val categories: List<Category> = emptyList(),
        val error: ErrorType? = null,
    )

    fun loadCategories() {
        viewModelScope.launch {
            _state.value = _state.value?.copy(error = null)

            val cachedCategories = recipesRepository.getCategoriesFromCache()

            if (cachedCategories.isNotEmpty()) {
                val updatedCategories = cachedCategories.map { category ->
                    category.copy(imageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + category.imageUrl)
                }
                _state.value = _state.value?.copy(
                    categories = updatedCategories,
                )
            }

            val networkCategories = recipesRepository.getCategories()
            if (networkCategories != null) {
                recipesRepository.saveCategoriesToCache(networkCategories)

                val updatedCategories = networkCategories.map { category ->
                    category.copy(imageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + category.imageUrl)
                }
                _state.value = _state.value?.copy(
                    categories = updatedCategories,
                    error = null,
                )
            } else {
                _state.value = _state.value?.copy(
                    error = ErrorType.DATA_FETCH_ERROR,
                )
            }
        }
    }

    fun clearError() {
        _state.postValue(_state.value?.copy(error = null))
    }
}