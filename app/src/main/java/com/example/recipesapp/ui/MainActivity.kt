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
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), OnNavigationListener {

    companion object {
        private const val CONNECTION_TIMEOUT = 10_000L
        private const val READ_TIMEOUT = 10_000L
        private const val CATEGORY_API_URL = "https://recipes.androidsprint.ru/api/category"
        private const val RECIPES_API_URL = "https://recipes.androidsprint.ru/api/category/%d/recipes"
    }

    private val threadPool: ExecutorService = Executors.newFixedThreadPool(10)

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .build()
    }

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

        getCategoriesAndRecipes()
    }

    private fun getCategoriesAndRecipes() {
        threadPool.execute {
            val requestThreadName = Thread.currentThread().name
            Log.d("MainActivity", "Выполняю запрос категорий на потоке: $requestThreadName")

            val request = Request.Builder()
                .url(CATEGORY_API_URL)
                .build()

            try {
                okHttpClient.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        response.body?.string()?.let { responseBody ->
                            Log.d(
                                "MainActivity",
                                "Ответ от сервера (категории): $responseBody"
                            )

                            val gson = Gson()
                            val listType = object : TypeToken<List<Category>>() {}.type
                            val categories: List<Category> =
                                gson.fromJson(responseBody, listType)

                            categories.forEach { category ->
                                Log.d(
                                    "MainActivity",
                                    "Категория id=${category.id}, title=${category.title}, description=${category.description}, imageUrl=${category.imageUrl}"
                                )
                            }

                            val categoriesIds = categories.map { it.id }
                            categoriesIds.forEach { categoryId ->
                                threadPool.execute {
                                    getRecipesByCategoryId(categoryId)
                                }
                            }
                        }

                    } else {
                        Log.e("MainActivity", "Ошибка при запросе категорий. Ответ: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Ошибка при выполнении запроса категорий", e)
            }
        }
    }

    private fun getRecipesByCategoryId(categoryId: Int) {
        val threadName = Thread.currentThread().name
        Log.d(
            "MainActivity",
            "Выполняю запрос рецептов для категории $categoryId на потоке: $threadName"
        )

        val recipesUrl = String.format(Locale.getDefault(), RECIPES_API_URL, categoryId)
        val request = Request.Builder()
            .url(recipesUrl)
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        Log.d(
                            "MainActivity",
                            "Ответ от сервера (рецепты для категории $categoryId): $responseBody"
                        )

                        val gson = Gson()
                        val listType = object : TypeToken<List<Recipe>>() {}.type
                        val recipes: List<Recipe> = gson.fromJson(responseBody, listType)

                        recipes.forEach { recipe ->
                            Log.d(
                                "MainActivity",
                                "Рецепт id=${recipe.id}, title=${recipe.title}, ingredients=${recipe.ingredients}, method=${recipe.method},imageUrl=${recipe.imageUrl}"
                            )
                        }
                    }
                } else {
                    Log.e(
                        "MainActivity",
                        "Ошибка при запросе рецептов для категории $categoryId. Ответ: ${response.code}"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(
                "MainActivity",
                "Ошибка при выполнении запроса рецептов для категории $categoryId",
                e
            )
        }
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
        threadPool.shutdown()
    }
}