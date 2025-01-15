package com.example.recipesapp.ui.categories
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recipesapp.common.Constants
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.ErrorType
import kotlinx.coroutines.launch

class CategoriesListViewModel(
    private val recipesRepository: RecipesRepository,
) : ViewModel() {

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

            if (cachedCategories.isEmpty()) {
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
            } else {
                val updatedCategories = cachedCategories.map { category ->
                    category.copy(imageUrl = Constants.BASE_URL + Constants.IMAGES_PATH + category.imageUrl)
                }
                _state.value = _state.value?.copy(
                    categories =  updatedCategories,
                )
            }
        }
    }

    fun clearError() {
        _state.postValue(_state.value?.copy(error = null))
    }
}