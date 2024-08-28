package com.example.recipesapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.recipesapp.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _favoritesBinding: FragmentFavoritesBinding? = null
    private val favoritesBinding: FragmentFavoritesBinding
        get() = _favoritesBinding
            ?: throw IllegalStateException("Binding for FragmentFavoritesBinding must not be null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _favoritesBinding = FragmentFavoritesBinding.inflate(inflater, container, false)
        val view = favoritesBinding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _favoritesBinding = null
    }
}