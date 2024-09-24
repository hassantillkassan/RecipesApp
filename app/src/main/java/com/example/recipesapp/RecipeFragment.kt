package com.example.recipesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.recipesapp.databinding.FragmentRecipeBinding


class RecipeFragment : Fragment() {

    private var _recipeBinding: FragmentRecipeBinding? = null
    private val recipeBinding: FragmentRecipeBinding
        get() = _recipeBinding
            ?: throw IllegalStateException("Binding for FragmentRecipeBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _recipeBinding = FragmentRecipeBinding.inflate(inflater, container, false)
        return recipeBinding.root
    }

    companion object {
        private const val ARG_RECIPE_ID = "recipe_id"

        fun newInstance(recipeId: Int) : RecipeFragment {
            return RecipeFragment().apply {
                arguments = bundleOf(ARG_RECIPE_ID to recipeId)
            }
        }
    }
}