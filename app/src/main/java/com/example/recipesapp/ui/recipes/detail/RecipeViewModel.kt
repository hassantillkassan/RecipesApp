package com.example.recipesapp.ui.recipes.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.recipesapp.model.Recipe

class RecipeViewModel : ViewModel() {

    private val _state = MutableLiveData<RecipeState>()
    val state: LiveData<RecipeState>
        get() = _state

    data class RecipeState(
        val recipe: Recipe? = null,
        val portionCount: Int = 1,
        val isFavorite: Boolean = false,
    )

    init {
        Log.i("RecipeViewModel", "ViewModel initialization")
        _state.value = RecipeState(isFavorite = false)
    }

}