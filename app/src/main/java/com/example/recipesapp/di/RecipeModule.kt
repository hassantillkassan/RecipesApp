package com.example.recipesapp.di

import android.content.Context
import androidx.room.Room
import com.example.recipesapp.common.Constants
import com.example.recipesapp.data.AppDatabase
import com.example.recipesapp.data.CategoriesDao
import com.example.recipesapp.data.RecipeApiService
import com.example.recipesapp.data.RecipesDao
import com.example.recipesapp.data.RecipesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
class RecipeModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "database-categories"
    ).build()

    @Provides
    fun provideCategoriesDao(appDatabase: AppDatabase) : CategoriesDao = appDatabase.categoriesDao()

    @Provides
    fun provideRecipesDao(appDatabase: AppDatabase) : RecipesDao = appDatabase.recipesDao()

    @Provides
    fun provideHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }

    @Provides
    fun provideRecipeApiService(retrofit: Retrofit): RecipeApiService {
        return retrofit.create(RecipeApiService::class.java)
    }
}