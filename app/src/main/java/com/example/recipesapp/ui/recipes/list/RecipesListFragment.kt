package com.example.recipesapp.ui.recipes.list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.R
import com.example.recipesapp.RecipeApplication
import com.example.recipesapp.common.loadImage
import com.example.recipesapp.databinding.FragmentListRecipesBinding
import com.example.recipesapp.di.AppContainer
import com.example.recipesapp.model.ErrorType
import com.example.recipesapp.ui.OnNavigationListener
import com.example.recipesapp.ui.RecipesListAdapter

class RecipesListFragment : Fragment() {

    private var _recipesBinding: FragmentListRecipesBinding? = null
    private val recipesBinding: FragmentListRecipesBinding
        get() = _recipesBinding
            ?: throw IllegalStateException("Binding for FragmentListRecipesBinding must not be null")

    private val navigationListener: OnNavigationListener by lazy {
        (activity as? OnNavigationListener)
            ?: throw RuntimeException(
                "${requireActivity()::class.qualifiedName} must implement OnNavigationListener"
            )
    }

    private lateinit var recipesListViewModel: RecipesListViewModel

    private var recipesAdapter: RecipesListAdapter? = null

    private val args: RecipesListFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer: AppContainer = (requireActivity().application as RecipeApplication).appContainer
        recipesListViewModel = appContainer.recipesListViewModelFactory.create()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _recipesBinding = FragmentListRecipesBinding.inflate(inflater, container, false)
        return recipesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val category = args.category
        recipesListViewModel.loadRecipe(category)

        recipesAdapter = RecipesListAdapter(emptyList()) { recipe ->
            navigationListener.navigateToRecipe(recipe)
        }

        with(recipesBinding.rvRecipes) {
            adapter = recipesAdapter
            layoutManager = LinearLayoutManager(context)
        }

        recipesListViewModel.state.observe(viewLifecycleOwner) { state ->
            recipesBinding.tvCategoryName.text = state.categoryName

            state.categoryImage?.let { imageUrl ->
                recipesBinding.ivCategoryCoverImage.loadImage(imageUrl)
            }

            recipesBinding.ivCategoryCoverImage.contentDescription = getString(
                R.string.text_category_image_description,
                category.title
            )

            recipesAdapter?.updateData(state.recipes)

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

                recipesListViewModel.clearError()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _recipesBinding = null
    }

    companion object {
        fun newInstance(args: Bundle) = RecipesListFragment().apply {
            arguments = args
        }
    }

}