package com.example.recipesapp.ui.recipes.favorites

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentFavoritesBinding
import com.example.recipesapp.model.ErrorType
import com.example.recipesapp.ui.OnNavigationListener
import com.example.recipesapp.ui.RecipesListAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment() {

    private var _favoritesBinding: FragmentFavoritesBinding? = null
    private val favoritesBinding: FragmentFavoritesBinding
        get() = _favoritesBinding
            ?: throw IllegalStateException("Binding for FragmentFavoritesBinding must not be null")


    private val favoritesViewModel: FavoritesViewModel by viewModels()

    private var recipesAdapter: RecipesListAdapter? = null

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

        favoritesViewModel.state.observe(viewLifecycleOwner) { state ->
            recipesAdapter?.updateData(state.recipes)

            when {
                state.isLoading -> {
                    favoritesBinding.rvFavorites.visibility = View.GONE
                    favoritesBinding.tvEmptyFavorites.visibility = View.GONE
                }

                state.isEmpty -> {
                    favoritesBinding.rvFavorites.visibility = View.GONE
                    favoritesBinding.tvEmptyFavorites.visibility = View.VISIBLE
                }

                state.error != null -> {
                    favoritesBinding.rvFavorites.visibility = View.GONE
                    favoritesBinding.tvEmptyFavorites.visibility = View.GONE

                    val errorMessage = when (state.error) {
                        ErrorType.DATA_FETCH_ERROR -> getString(R.string.error_data_fetch)
                        ErrorType.UNKNOWN_ERROR -> getString(R.string.error_unknown)
                    }
                    Toast.makeText(
                        requireContext(),
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()

                    favoritesViewModel.clearError()
                }

                else -> {
                    favoritesBinding.rvFavorites.visibility = View.VISIBLE
                    favoritesBinding.tvEmptyFavorites.visibility = View.GONE
                }
            }
        }

        favoritesViewModel.loadFavorites()
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
        val favoritesState = favoritesViewModel.state.value
        val selectedRecipe = favoritesState?.recipes?.find { it.id == recipeId }

        if (selectedRecipe != null) {
            Log.d("FavoritesFragment", "Переход на рецепт: ID = ${selectedRecipe.id}, Избранное = ${selectedRecipe.isFavorite}")
            (activity as? OnNavigationListener)?.navigateToRecipe(selectedRecipe)
        } else {
            Log.e("FavoritesFragment", "Рецепт с ID = $recipeId не найден")
            Toast.makeText(
                context,
                getString(R.string.warning_recipe_not_found),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _favoritesBinding = null
    }
}