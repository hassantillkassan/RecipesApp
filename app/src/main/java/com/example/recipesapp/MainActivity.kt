package com.example.recipesapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
        navigateToFragment<CategoriesListFragment>()
    }

    override fun navigateToFavorites() {
        navigateToFragment<FavoritesFragment>()
    }

    override fun navigateToRecipes(bundle: Bundle) {
        val recipesListFragment = RecipesListFragment.newInstance(
            bundle.getInt(RecipesListFragment.ARG_CATEGORY_ID),
            bundle.getString(RecipesListFragment.ARG_CATEGORY_NAME) ?: "",
            bundle.getString(RecipesListFragment.ARG_CATEGORY_IMAGE_URL) ?: ""
        )
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.mainContainer, recipesListFragment)
            addToBackStack(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private inline fun <reified T : Fragment> navigateToFragment() {
        supportFragmentManager.popBackStack()

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<T>(binding.mainContainer.id)
            addToBackStack(null)
        }
    }
}