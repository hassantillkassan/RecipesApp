package com.example.recipesapp

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.databinding.FragmentListRecipesBinding
import java.io.InputStream

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

    private var categoryId: Int = 0
    private var categoryName: String = ""
    private var categoryImageUrl: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _recipesBinding = FragmentListRecipesBinding.inflate(inflater, container, false)
        return recipesBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { bundle ->
            categoryId = bundle.getInt(CategoriesListFragment.ARG_CATEGORY_ID)
            categoryName = bundle.getString(CategoriesListFragment.ARG_CATEGORY_NAME) ?: ""
            categoryImageUrl = bundle.getString(CategoriesListFragment.ARG_CATEGORY_IMAGE_URL) ?: ""
        }

        recipesBinding.tvCategoryName.text = categoryName

        categoryImageUrl.let { imageUrl ->
            try {
                val inputStream: InputStream? = context?.assets?.open(imageUrl)
                val drawable = Drawable.createFromStream(inputStream, null)
                recipesBinding.ivCategoryCoverImage.setImageDrawable(drawable)
                inputStream?.close()
            } catch (e: Exception) {
                Log.e("RecipesListFragment", "Image not found $imageUrl", e)
            }
        }
        recipesBinding.ivCategoryCoverImage.contentDescription = getString(
            R.string.category_image_description,
            categoryName
        )

        initRecycler()
    }

    private fun initRecycler() {
        val recipes = STUB.getRecipesByCategoryId(categoryId)

        val adapter = RecipesListAdapter(recipes) { recipeId ->
            navigationListener.openRecipeByRecipeId(recipeId)
        }

        recipesBinding.rvRecipes.adapter = adapter
        recipesBinding.rvRecipes.layoutManager = LinearLayoutManager(context)
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