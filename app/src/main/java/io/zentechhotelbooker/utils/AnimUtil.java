package io.zentechhotelbooker.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import io.zentechhotelbooker.R;

public class AnimUtil {

    //...
    /**
     *
     * @param ctx
     * @param v
     */

    public static void slide_down(Context ctx, View v){
        Animation a = AnimationUtils.loadAnimation(ctx,R.anim.slide_down);
        if(a != null){
            a.reset();
            if(v != null){
                v.clearAnimation();
                v.startAnimation(a);
            }
        }
    }


}
