package com.example.recipesapp.di

import android.content.Context
import androidx.room.Room
import com.example.recipesapp.common.Constants
import com.example.recipesapp.data.AppDatabase
import com.example.recipesapp.data.RecipeApiService
import com.example.recipesapp.data.RecipesRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(context: Context) {

    private val db: AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "database-categories"
    ).build()

    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO


    private val categoriesDao = db.categoriesDao()
    private val recipesDao = db.recipesDao()

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val recipeApiService: RecipeApiService = retrofit.create(RecipeApiService::class.java)

    val repository = RecipesRepository(
        recipesDao = recipesDao,
        categoriesDao = categoriesDao,
        recipeApiService = recipeApiService,
        ioDispatcher = ioDispatcher
    )

    val categoriesListViewModelFactory: CategoriesListViewModelFactory by lazy {
        CategoriesListViewModelFactory(repository)
    }

    val recipesListViewModelFactory: RecipesListViewModelFactory by lazy {
        RecipesListViewModelFactory(repository)
    }

    val favoritesViewModelFactory: FavoritesViewModelFactory by lazy {
        FavoritesViewModelFactory(repository)
    }

    val recipeViewModelFactory: RecipeViewModelFactory by lazy {
        RecipeViewModelFactory(repository)
    }
}