package com.example.twilightlemon.lemonapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.RequiresApi;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;

public class AnimatorUtil {
    public static void animHeightToView(AnimatorListenerAdapter ds,final View v, final int start, final int end, long duration) {

        ValueAnimator va = ValueAnimator.ofInt(start, end);
        va.setInterpolator(new DecelerateInterpolator());
        final ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int h = (int) animation.getAnimatedValue();
                layoutParams.height = h;
                v.setLayoutParams(layoutParams);
                v.requestLayout();
            }
        });
        if(ds!=null)
           va.addListener(ds);
        va.setDuration(duration);
        va.start();
    }
    public static void animop(View v,float f,float t,int d){
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, t);
        alphaAnimation.setDuration(d);
        v.startAnimation(alphaAnimation);
    }
}

