package com.example.recipesapp.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.ThreadPoolProvider
import com.example.recipesapp.data.RecipesRepository
import com.example.recipesapp.model.Category

class CategoriesListViewModel(application: Application) : AndroidViewModel(application) {

    private val recipesRepository = RecipesRepository()

    private val executor = ThreadPoolProvider.threadPool

    private val _state = MutableLiveData(CategoriesListState())
    val state: LiveData<CategoriesListState>
        get() = _state

    data class CategoriesListState(
        val categories: List<Category> = emptyList(),
        val errorMessage: String? = null,
    )

    fun loadCategories() {
        executor.execute {
            val categories = recipesRepository.getCategories()

            if (categories != null) {
                _state.postValue(
                    _state.value?.copy(
                        categories = categories,
                    )
                )
            } else {
                _state.postValue(
                    _state.value?.copy(
                        errorMessage = "Ошибка получения данных",
                    )
                )
            }
        }
    }
}