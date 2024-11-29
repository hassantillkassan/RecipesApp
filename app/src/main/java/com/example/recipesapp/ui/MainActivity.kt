package com.example.recipesapp.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.recipesapp.R
import com.example.recipesapp.databinding.ActivityMainBinding
import com.example.recipesapp.model.Category
import com.example.recipesapp.model.Recipe
import com.example.recipesapp.ui.categories.CategoriesListFragmentDirections
import com.example.recipesapp.ui.recipes.favorites.FavoritesFragmentDirections
import com.example.recipesapp.ui.recipes.list.RecipesListFragmentDirections
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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

        val mainThreadName = Thread.currentThread().name
        Log.d("MainActivity", "Метод onCreate() выполняется на потоке: $mainThreadName")

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

        Thread {
            val requestThreadName = Thread.currentThread().name
            Log.d("MainActivity", "Выполняю запрос на потоке: $requestThreadName")

            val url = URL("https://recipes.androidsprint.ru/api/category")
            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = "GET"
                connection.connectTimeout = 10000
                connection.readTimeout = 10000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val stream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(stream))
                    val response = StringBuilder()

                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    reader.close()
                    stream.close()
                    connection.disconnect()

                    Log.d("MainActivity", "Ответ от сервера: ${response.toString()}")

                    val gson = Gson()
                    val listType = object : TypeToken<List<Category>>() {}.type
                    val categories: List<Category> = gson.fromJson(response.toString(), listType)

                    categories.forEach { category ->
                        Log.d(
                            "MainActivity",
                            "Категория id=${category.id}, title=${category.title}, description=${category.description}, imageUrl=${category.imageUrl}"
                        )
                    }
                } else {
                    Log.e("MainActivity", "Ошибка при запросе. Ответ: $responseCode")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Ошибка при выполнении запроса", e)
            } finally {
                connection.disconnect()
            }
        }.start()
    }

    override fun navigateToCategories() {
        if (navController.currentDestination?.id != R.id.categoriesListFragment) {
            navController.navigate(
                R.id.action_global_categoriesListFragment,
                null,
            )
        }
    }

    override fun navigateToFavorites() {
        if (navController.currentDestination?.id != R.id.favoritesFragment) {
            navController.navigate(
                R.id.action_global_favoritesFragment,
                null,
            )
        }
    }

    override fun navigateToRecipesList(category: Category) {
        val action = CategoriesListFragmentDirections
            .actionCategoriesListFragmentToRecipesListFragment(category)
        navController.navigate(action)
    }

    override fun navigateToRecipe(recipe: Recipe) {
        val direction = when (navController.currentDestination?.id) {
            R.id.favoritesFragment -> FavoritesFragmentDirections.actionFavoritesFragmentToRecipeFragment(
                recipe.id
            )

            R.id.recipesListFragment -> RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment(
                recipe.id
            )

            else -> null
        }

        if (direction != null) {
            navController.navigate(direction)
        } else {
            Log.e(
                "MainActivity",
                "No local action to navigate to RecipeFragment from the current location"
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}