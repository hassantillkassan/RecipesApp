package com.example.recipesapp.common

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.recipesapp.R

fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .placeholder(R.drawable.img_placeholder)
        .error(R.drawable.img_error)
        .into(this)
}