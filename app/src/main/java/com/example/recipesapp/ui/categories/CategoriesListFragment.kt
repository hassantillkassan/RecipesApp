package com.example.recipesapp.ui.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentListCategoriesBinding
import com.example.recipesapp.model.ErrorType
import com.example.recipesapp.ui.CategoriesListAdapter
import com.example.recipesapp.ui.OnNavigationListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

    private val categoriesListViewModel: CategoriesListViewModel by viewModels()

    private var categoriesAdapter: CategoriesListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        categoriesListViewModel.state.observe(viewLifecycleOwner) { state ->
            categoriesAdapter?.updateData(state.categories)

            state.error?.let { error ->
                val errorMessage = when (error) {
                    ErrorType.DATA_FETCH_ERROR -> getString(R.string.error_data_fetch)
                    ErrorType.UNKNOWN_ERROR -> getString(R.string.error_unknown)
                }
                Toast.makeText(
                    requireContext(),
                    errorMessage,
                    Toast.LENGTH_SHORT
                ).show()

                categoriesListViewModel.clearError()
            }
        }

        categoriesListViewModel.loadCategories()
    }

    private fun initRecycler() {
        categoriesAdapter = CategoriesListAdapter(emptyList()) { categoryId ->
            openRecipesByCategoryId(categoryId)
        }
        categoriesBinding.rvCategories.adapter = categoriesAdapter
    }

    private fun openRecipesByCategoryId(categoryId: Int) {
        val categoriesState = categoriesListViewModel.state.value
        val selectedCategory = categoriesState?.categories?.find { it.id == categoryId }

        if (selectedCategory != null) {
            navigationListener.navigateToRecipesList(selectedCategory)
        } else {
            Toast.makeText(
                requireContext(),
                "Категория не найдена",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _categoriesBinding = null
    }

}