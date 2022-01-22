package com.aggarwalankur.hubsearch.view

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.aggarwalankur.hubsearch.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class BindingUtils {
    class ViewBindingAdapter {

        companion object {
            @BindingAdapter("loadImg")
            @JvmStatic
            fun loadPhotoFromUrl(view: ImageView, url: String?) {
                url?.let {
                    Glide.with(view.context)
                        .load(it)
                        .placeholder(R.drawable.ic_unknown)
                        .error(R.drawable.ic_unknown)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(view)
                }
            }
        }
    }
}