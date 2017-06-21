package com.binartech.opolemap.activities;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.binartech.opolemap.R;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;

public class SplashActivity extends SherlockFragmentActivity implements Runnable
{
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        mHandler = new Handler();
        mHandler.postDelayed(this, 3000);
        final FrameLayout root = new FrameLayout(this);
        final ImageView view = new ImageView(this);
        view.setImageResource(R.drawable.splash);
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10f, getResources().getDisplayMetrics());
        root.addView(view, lp);
        root.setPadding(px, px, px, px);
        root.setBackgroundResource(R.color.splash_bg);
        setContentView(root);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mHandler.removeCallbacks(this);
    }

    @Override
    public void run()
    {
        startActivity(new Intent(this, CategoriesActivity.class));
        finish();
    }

}
