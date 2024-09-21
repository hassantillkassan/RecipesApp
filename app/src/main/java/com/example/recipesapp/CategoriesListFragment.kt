package com.example.recipesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.recipesapp.databinding.FragmentListCategoriesBinding

class CategoriesListFragment : Fragment() {

    private var _categoriesBinding: FragmentListCategoriesBinding? = null
    private val categoriesBinding: FragmentListCategoriesBinding
        get() = _categoriesBinding
            ?: throw IllegalStateException("Binding for FragmentListCategoriesBinding must not be null")

    private val navigationListener: OnNavigationListener by lazy {
        (activity as? OnNavigationListener)
            ?: throw RuntimeException(
                "${requireActivity()::class::qualifiedName} must implement OnNavigationListener"
            )
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

            val bundle = bundleOf(
                ARG_CATEGORY_ID to categoryId,
                ARG_CATEGORY_NAME to categoryName,
                ARG_CATEGORY_IMAGE_URL to categoryImageUrl
            )

            navigationListener.navigateToRecipes(bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _categoriesBinding = null
    }

    companion object {
        const val ARG_CATEGORY_ID = "category_id"
        const val ARG_CATEGORY_NAME = "category_name"
        const val ARG_CATEGORY_IMAGE_URL = "category_image_url"
    }

}