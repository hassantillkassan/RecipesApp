package com.example.recipesapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecipeViewModel: ViewModel() {
    private val _portions: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>().also { it.value = 1 }
    }

    val portions: LiveData<Int> get() = _portions

    fun setPortions(portions: Int) {
        _portions.value = portions
    }
}