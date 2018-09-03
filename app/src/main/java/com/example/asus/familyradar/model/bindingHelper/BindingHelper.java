package com.example.asus.familyradar.model.bindingHelper;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class BindingHelper {

    @BindingAdapter({"app:url"})
    public static void loadImage(CircleImageView view, String url){
        Glide.with(view.getContext())
                .load(url)
                .into(view);
    }

}
