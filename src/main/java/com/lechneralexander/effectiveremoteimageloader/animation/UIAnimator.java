package com.lechneralexander.effectiveremoteimageloader.animation;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.lechneralexander.effectiveremoteimageloader.R;


public class UIAnimator {
    public static void fadeInImage(ImageView imageView) {
        Animation fadeInAnimation = AnimationUtils.loadAnimation(imageView.getContext(), R.anim.image_fade_in);
        imageView.startAnimation(fadeInAnimation);
    }
}
