package com.example.recipesapp.ui.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.example.recipesapp.data.STUB
import com.example.recipesapp.databinding.FragmentListCategoriesBinding
import com.example.recipesapp.ui.CategoriesListAdapter
import com.example.recipesapp.ui.OnNavigationListener

class CategoriesListFragment : Fragment() {

    private var _categoriesBinding: FragmentListCategoriesBinding? = null
    private val categoriesBinding: FragmentListCategoriesBinding
        get() = _categoriesBinding
            ?: throw IllegalStateException("Binding for FragmentListCategoriesBinding must not be null")

    private val navigationListener: OnNavigationListener by lazy {
        (activity as? OnNavigationListener)
            ?: throw RuntimeException(
                "${requireActivity()::class.qualifiedName} must implement OnNavigationListener"
            )
    }

    private val viewModel: CategoriesListViewModel by viewModels()
    private var categoriesAdapter: CategoriesListAdapter? = null

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

        viewModel.state.observe(viewLifecycleOwner) { state ->
            categoriesAdapter?.updateData(state.categories)
        }

        viewModel.loadCategories()
    }

    private fun initRecycler() {
        categoriesAdapter = CategoriesListAdapter(emptyList()) { categoryId ->
            openRecipesByCategoryId(categoryId)
        }
        categoriesBinding.rvCategories.adapter = categoriesAdapter
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

            navigationListener.navigateToRecipesList(bundle)
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