package com.example.recipesapp

import android.graphics.drawable.Drawable
import android.os.Build
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
import com.example.recipesapp.databinding.FragmentRecipeBinding
import com.google.android.material.divider.MaterialDividerItemDecoration
import java.io.InputStream


class RecipeFragment : Fragment() {

    private var _recipeBinding: FragmentRecipeBinding? = null
    private val recipeBinding: FragmentRecipeBinding
        get() = _recipeBinding
            ?: throw IllegalStateException("Binding for FragmentRecipeBinding must not be null")

    private var recipe: Recipe? = null
    private var ingredientsAdapter: IngredientsAdapter? = null

    private val viewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        recipe = if (Build.VERSION.SDK_INT >= 33) {
            arguments?.getParcelable(ARG_RECIPE_ID, Recipe::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable(ARG_RECIPE_ID)
        } ?: throw IllegalArgumentException("Recipe must be provided in arguments")
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

        initRecycler()
        initUI()
        viewModel.portions.observe(viewLifecycleOwner) { portions ->
            ingredientsAdapter?.updateIngredients(portions)
        }
    }

    private fun initRecycler() {
        recipe?.let { recipe ->
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

            ingredientsAdapter = IngredientsAdapter(recipe.ingredients)
            recipeBinding.rvIngredients.adapter = ingredientsAdapter
            recipeBinding.rvIngredients.layoutManager = LinearLayoutManager(context)
            recipeBinding.rvIngredients.addItemDecoration(divider)

            val methodAdapter = MethodAdapter(recipe.method)
            recipeBinding.rvMethod.adapter = methodAdapter
            recipeBinding.rvMethod.layoutManager = LinearLayoutManager(context)
            recipeBinding.rvMethod.addItemDecoration(divider)

            recipeBinding.seekBarPortions.max = 5
            recipeBinding.seekBarPortions.min = 1
            recipeBinding.seekBarPortions.progress = 1

            recipeBinding.seekBarPortions.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val portions = if (progress < 1) 1 else progress
                    recipeBinding.tvPortionQuantity.text = getString(R.string.text_portions, portions)

//                    ingredientsAdapter.updateIngredients(portions)
                    viewModel.setPortions(portions)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar) {
                }
            })

            val initialPortions = recipeBinding.seekBarPortions.progress
            recipeBinding.tvPortionQuantity.text = getString(R.string.text_portions, initialPortions)
//            ingredientsAdapter.updateIngredients(initialPortions)
            viewModel.setPortions(initialPortions)
        }
    }

    private fun initUI() {
        recipe?.let { recipe ->
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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _recipeBinding = null
    }

    companion object {
        const val ARG_RECIPE_ID = "recipe_id"
    }
}