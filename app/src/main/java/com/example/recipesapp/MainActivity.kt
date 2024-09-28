package com.example.recipesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.recipesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), OnNavigationListener {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding
            ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<CategoriesListFragment>(R.id.mainContainer)
            }
        }

        binding.buttonCategory.setOnClickListener {
            navigateToCategories()
        }

        binding.buttonFavorites.setOnClickListener {
            navigateToFavorites()
        }
    }

    override fun navigateToCategories() {
        if (supportFragmentManager.findFragmentById(R.id.mainContainer) !is CategoriesListFragment)
            navigateToFragment<CategoriesListFragment>()
    }

    override fun navigateToFavorites() {
        if (supportFragmentManager.findFragmentById(R.id.mainContainer) !is FavoritesFragment)
            navigateToFragment<FavoritesFragment>()
    }

    override fun navigateToRecipesList(bundle: Bundle) {
        val recipesListFragment = RecipesListFragment.newInstance(bundle)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.mainContainer, recipesListFragment)
            addToBackStack(null)
        }
    }

    override fun navigateToRecipe(recipe: Recipe) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<RecipeFragment>(
                R.id.mainContainer,
                args = bundleOf(RecipeFragment.ARG_RECIPE_ID to recipe)
                )
            addToBackStack(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private inline fun <reified T : Fragment> navigateToFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<T>(binding.mainContainer.id)
            addToBackStack(null)
        }
    }
}