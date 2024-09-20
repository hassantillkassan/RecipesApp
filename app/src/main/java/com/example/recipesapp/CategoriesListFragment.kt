package com.example.recipesapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.recipesapp.databinding.FragmentListCategoriesBinding

class CategoriesListFragment : Fragment() {

    private var _categoriesBinding: FragmentListCategoriesBinding? = null
    private val categoriesBinding: FragmentListCategoriesBinding
        get() = _categoriesBinding
            ?: throw IllegalStateException("Binding for FragmentListCategoriesBinding must not be null")

    private var navigationListener: OnNavigationListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnNavigationListener) {
            navigationListener = context
        } else {
            throw RuntimeException("$context must implement OnNavigationListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _categoriesBinding = FragmentListCategoriesBinding.inflate(inflater, container, false)
        return categoriesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecycler()
    }

    private fun initRecycler() {
        val categories = STUB.getCategories()

        val adapter = CategoriesListAdapter(categories) { categoryId ->
            openRecipesByCategoryId(categoryId)
        }
        categoriesBinding.rvCategories.adapter = adapter
    }

    private fun openRecipesByCategoryId(categoryId: Int) {
        val categories = STUB.getCategories()
        val selectedCategory = categories.find { it.id == categoryId }

        if (selectedCategory != null) {
            val categoryName = selectedCategory.title
            val categoryImageUrl = selectedCategory.imageUrl

            val bundle = Bundle().apply {
                putInt(RecipesListFragment.ARG_CATEGORY_ID, categoryId)
                putString(RecipesListFragment.ARG_CATEGORY_NAME, categoryName)
                putString(RecipesListFragment.ARG_CATEGORY_IMAGE_URL, categoryImageUrl)
            }

            navigationListener?.navigateToRecipes(bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _categoriesBinding = null
    }

}