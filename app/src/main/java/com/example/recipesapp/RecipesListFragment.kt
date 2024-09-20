package com.example.recipesapp

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.recipesapp.databinding.FragmentListRecipesBinding
import java.io.InputStream

class RecipesListFragment : Fragment() {

    private var _recipesBinding: FragmentListRecipesBinding? = null
    private val recipesBinding: FragmentListRecipesBinding
        get() = _recipesBinding
            ?: throw IllegalStateException("Binding for FragmentListRecipesBinding must not be null")

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

        categoryId = requireArguments().getInt(ARG_CATEGORY_ID)
        categoryName = requireArguments().getString(ARG_CATEGORY_NAME) ?: ""
        categoryImageUrl = requireArguments().getString(ARG_CATEGORY_IMAGE_URL) ?: ""

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

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _recipesBinding = null
    }

    companion object {
        const val ARG_CATEGORY_ID = "category_id"
        const val ARG_CATEGORY_NAME = "category_name"
        const val ARG_CATEGORY_IMAGE_URL = "category_image_url"

        fun newInstance(
            categoryId: Int,
            categoryName: String,
            categoryImageUrl: String,
        ): RecipesListFragment {
            val fragment = RecipesListFragment()
            val args = Bundle().apply {
                putInt(ARG_CATEGORY_ID, categoryId)
                putString(ARG_CATEGORY_NAME, categoryName)
                putString(ARG_CATEGORY_IMAGE_URL, categoryImageUrl)
            }

            fragment.arguments = args
            return fragment
        }
    }

}