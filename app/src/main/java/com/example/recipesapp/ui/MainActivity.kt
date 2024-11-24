package com.example.recipesapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.example.recipesapp.R
import com.example.recipesapp.databinding.ActivityMainBinding
import com.example.recipesapp.model.Recipe
import com.example.recipesapp.ui.recipes.detail.RecipeFragment

class MainActivity : AppCompatActivity(), OnNavigationListener {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        navController = navHostFragment.navController

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.buttonCategory.setOnClickListener {
            navigateToCategories()
        }

        binding.buttonFavorites.setOnClickListener {
            navigateToFavorites()
        }
    }

    override fun navigateToCategories() {
        if (navController.currentDestination?.id != R.id.categoriesListFragment) {
            navController.navigate(
                R.id.action_global_categoriesListFragment,
                null,
                navOptions {
                    launchSingleTop = true
                }
            )
        }
    }

    override fun navigateToFavorites() {
        if (navController.currentDestination?.id != R.id.favoritesFragment) {
            navController.navigate(
                R.id.action_global_favoritesFragment,
                null,
                navOptions {
                    launchSingleTop = true
                }
            )
        }
    }

    override fun navigateToRecipesList(bundle: Bundle) {
        navController.navigate(R.id.action_categoriesListFragment_to_recipesListFragment, bundle)
    }

    override fun navigateToRecipe(recipe: Recipe) {
        val bundle = Bundle().apply {
            putInt(RecipeFragment.ARG_RECIPE_ID, recipe.id)
        }
        navController.navigate(R.id.action_global_recipeFragment, bundle)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}