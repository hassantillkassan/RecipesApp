package com.example.recipesapp.ui.categories

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.common.Constants
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.ErrorType
import kotlinx.coroutines.launch

class CategoriesListViewModel(application: Application) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository()

    private val _state = MutableLiveData(CategoriesListState())
    val state: LiveData<CategoriesListState>
        get() = _state

    data class CategoriesListState(
        val categories: List<Category> = emptyList(),
        val error: ErrorType? = null,
    )

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val categories = recipesRepository.getCategories()

                if (categories != null) {
                    val updatedCategories = categories.map { category ->
                        category.copy(imageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + category.imageUrl)
                    }

                    _state.postValue(
                        _state.value?.copy(
                            categories = updatedCategories,
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
                Log.e("CategoriesListViewModel", "Ошибка при загрузке категорий", e)
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