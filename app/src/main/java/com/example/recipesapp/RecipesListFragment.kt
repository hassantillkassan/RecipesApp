package com.example.recipesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.recipesapp.databinding.FragmentListRecipesBinding

class RecipesListFragment : Fragment() {

    private var _recipesBinding: FragmentListRecipesBinding? = null
    private val recipesBinding: FragmentListRecipesBinding
        get() = _recipesBinding
            ?: throw IllegalStateException("Binding for FragmentListRecipesBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _recipesBinding = FragmentListRecipesBinding.inflate(inflater, container, false)
        return recipesBinding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _recipesBinding = null
    }
}