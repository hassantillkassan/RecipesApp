package com.example.recipesapp.ui.categories

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.recipesapp.data.STUB
import com.example.recipesapp.model.Category

class CategoriesListViewModel(application: Application) : AndroidViewModel(application) {

    private val _state = MutableLiveData(CategoriesListState())
    val state: LiveData<CategoriesListState>
        get() = _state

    data class CategoriesListState(
        val categories: List<Category> = emptyList(),
    )

    fun loadCategories() {
        val categories = STUB.getCategories()
        _state.value = _state.value?.copy(
            categories = categories,
        )
    }
}