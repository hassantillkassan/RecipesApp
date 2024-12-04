package com.example.recipesapp.ui.recipes.favorites

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentFavoritesBinding
import com.example.recipesapp.ui.OnNavigationListener
import com.example.recipesapp.ui.RecipesListAdapter

class FavoritesFragment : Fragment() {

    private var _favoritesBinding: FragmentFavoritesBinding? = null
    private val favoritesBinding: FragmentFavoritesBinding
        get() = _favoritesBinding
            ?: throw IllegalStateException("Binding for FragmentFavoritesBinding must not be null")

    private var recipesAdapter: RecipesListAdapter? = null
    private val viewModel: FavoritesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _favoritesBinding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return favoritesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeRecyclerView()

        viewModel.state.observe(viewLifecycleOwner) { state ->
            recipesAdapter?.updateData(state.recipes)

            if (state.isEmpty) {
                favoritesBinding.rvFavorites.visibility = View.GONE
                favoritesBinding.tvEmptyFavorites.visibility = View.VISIBLE
            } else {
                favoritesBinding.rvFavorites.visibility = View.VISIBLE
                favoritesBinding.tvEmptyFavorites.visibility = View.GONE
            }
        }

        viewModel.loadFavorites()
    }

    private fun initializeRecyclerView() {
        recipesAdapter = RecipesListAdapter(emptyList()) {recipe ->
            openRecipeByRecipeId(recipe.id)
        }

        with(favoritesBinding.rvFavorites) {
            layoutManager = LinearLayoutManager(context)
            adapter = recipesAdapter
        }
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        val favoritesState = viewModel.state.value
        val selectedRecipe = favoritesState?.recipes?.find { it.id == recipeId }

        if (selectedRecipe != null) {
            (activity as? OnNavigationListener)?.navigateToRecipe(selectedRecipe)
        } else {
            Toast.makeText(
                context,
                getString(R.string.warning_recipe_not_found),
                Toast.LENGTH_SHORT
            ).show()
        }

        /*val recipe = STUB.getRecipeById(recipeId)
        if (recipe != null) {
            (activity as? OnNavigationListener)?.navigateToRecipe(recipe)
        } else {
            Toast.makeText(context, getString(R.string.warning_recipe_not_found), Toast.LENGTH_SHORT).show()
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _favoritesBinding = null
    }
}