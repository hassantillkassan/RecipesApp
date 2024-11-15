package com.example.recipesapp.ui.recipes.detail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipesapp.R
import com.example.recipesapp.databinding.FragmentRecipeBinding
import com.example.recipesapp.ui.IngredientsAdapter
import com.example.recipesapp.ui.MethodAdapter
import com.google.android.material.divider.MaterialDividerItemDecoration
import java.io.InputStream


class RecipeFragment : Fragment() {

    private var _recipeBinding: FragmentRecipeBinding? = null
    private val recipeBinding: FragmentRecipeBinding
        get() = _recipeBinding
            ?: throw IllegalStateException("Binding for FragmentRecipeBinding must not be null")

    private val viewModel: RecipeViewModel by viewModels()

    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipeId = arguments?.getInt(ARG_RECIPE_ID)
            ?: throw IllegalArgumentException("Recipe ID must be provided in arguments")

        viewModel.loadRecipe(recipeId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _recipeBinding = FragmentRecipeBinding.inflate(inflater, container, false)
        return recipeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recipeBinding.btnFavorite.setOnClickListener {
            viewModel.onFavoritesClicked()
        }

        initUI()
        initRecycler()
    }

    private fun initRecycler() {
        val recipe = viewModel.state.value?.recipe ?: return

        val dividerColor = ContextCompat.getColor(requireContext(), R.color.dividerColor)
        val divider = MaterialDividerItemDecoration(
            requireContext(),
            LinearLayoutManager.VERTICAL
        ).apply {
            dividerThickness = resources.getDimensionPixelSize(R.dimen.divider_thickness)
            this.dividerColor = dividerColor
            dividerInsetStart = resources.getDimensionPixelSize(R.dimen.main_space_12)
            dividerInsetEnd = resources.getDimensionPixelSize(R.dimen.main_space_12)
            isLastItemDecorated = false
        }

        val ingredientsAdapter = IngredientsAdapter(recipe.ingredients)
        recipeBinding.rvIngredients.apply {
            adapter = ingredientsAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(divider)
        }

        val methodAdapter = MethodAdapter(recipe.method)
        recipeBinding.rvMethod.apply {
            adapter = methodAdapter
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(divider)
        }

        recipeBinding.seekBarPortions.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar?,
                progress: Int,
                fromUser: Boolean
            ) {
                recipeBinding.tvPortionQuantity.text =
                    getString(R.string.text_portions, progress)

                ingredientsAdapter.updateIngredients(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })

        val initialPortions = recipeBinding.seekBarPortions.progress
        recipeBinding.tvPortionQuantity.text =
            getString(R.string.text_portions, initialPortions)
        ingredientsAdapter.updateIngredients(initialPortions)
    }

    private fun initUI() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            state.recipe?.let { recipe ->
                recipeBinding.tvRecipe.text = recipe.title

                try {
                    val inputStream: InputStream = requireContext().assets.open(recipe.imageUrl)
                    val drawable = Drawable.createFromStream(inputStream, null)
                    recipeBinding.ivRecipeImage.setImageDrawable(drawable)
                    inputStream.close()
                } catch (e: Exception) {
                    Log.e("RecipeFragment", "Image not found ${recipe.imageUrl}", e)
                    recipeBinding.ivRecipeImage.setImageResource(R.drawable.burger)
                }

                recipeBinding.ivRecipeImage.contentDescription = getString(
                    R.string.text_recipe_image_description,
                    recipe.title
                )

                updateFavoriteIcon(state.isFavorite)
            }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite)
            recipeBinding.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
        else
            recipeBinding.btnFavorite.setImageResource(R.drawable.ic_heart_empty)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _recipeBinding = null
    }

    companion object {
        const val ARG_RECIPE_ID = "recipe_id"
    }
}