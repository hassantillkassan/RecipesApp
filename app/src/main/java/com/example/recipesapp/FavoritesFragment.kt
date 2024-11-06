package com.example.recipesapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _favoritesBinding: FragmentFavoritesBinding? = null
    private val favoritesBinding: FragmentFavoritesBinding
        get() = _favoritesBinding
            ?: throw IllegalStateException("Binding for FragmentFavoritesBinding must not be null")

    private lateinit var recipesAdapter: RecipesListAdapter

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
        loadFavoritesRecipes()
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

    private fun loadFavoritesRecipes() {
        val favoritesIds = getFavorites()

        if (favoritesIds.isNotEmpty()) {
            val favoriteRecipes = STUB.getRecipesByIds(favoritesIds)
            if (favoritesIds.isNotEmpty()) {
                recipesAdapter = RecipesListAdapter(favoriteRecipes) {recipe ->
                    openRecipeByRecipeId(recipe.id)
                }
                favoritesBinding.rvFavorites.adapter = recipesAdapter
                favoritesBinding.rvFavorites.visibility = View.VISIBLE
                favoritesBinding.tvEmptyFavorites.visibility = View.GONE
            } else {
                favoritesBinding.rvFavorites.visibility = View.GONE
                favoritesBinding.tvEmptyFavorites.visibility = View.VISIBLE
            }
        }
    }

    private fun getFavorites(): Set<Int> {
        val sharedPreferences =
            requireContext().getSharedPreferences("favorite_recipes_prefs", Context.MODE_PRIVATE)
        val favoriteIdsStringSet = sharedPreferences.getStringSet("favorites_recipes", emptySet()) ?: emptySet()

        return favoriteIdsStringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    private fun openRecipeByRecipeId(recipeId: Int) {
        val recipe = STUB.getRecipeById(recipeId)
        if (recipe != null) {
            (activity as? OnNavigationListener)?.navigateToRecipe(recipe)
        } else {
            Toast.makeText(context, getString(R.string.warning_recipe_not_found), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _favoritesBinding = null
    }
}