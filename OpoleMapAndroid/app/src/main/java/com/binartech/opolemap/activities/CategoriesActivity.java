package com.binartech.opolemap.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import asia.ivity.android.tiledscrollview.TiledScrollView.ZoomLevel;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.binartech.opolemap.R;
import com.binartech.opolemap.core.PoiStore;
import com.binartech.opolemap.fragments.TiledMapFragment;

public class CategoriesActivity extends SherlockFragmentActivity
{
	
	public static String category;
	public static int poi;
	
    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_categories);
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	if(getResources().getBoolean(R.bool.isTablet))
        {
        	TiledMapFragment mMap = (TiledMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        	mMap.setZoom(ZoomLevel.LEVEL_1);
        	SharedPreferences pref = getPreferences(MODE_PRIVATE);
        	final String category = pref.getString("category",null );
        	final int poi = pref.getInt("poi", 0);
        	if(category != null) {
        		try {
        			mMap.setDisplayedCategory(category, poi);
        		} catch (Exception e) {
        			e.printStackTrace();
        			mMap.setDisplayedCategory(PoiStore.getCategoryAll(), 0);
        		}
        	}
        }
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	SharedPreferences pref = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("category", category);
		editor.putInt("poi", 0);
		editor.commit();
    }
    
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
