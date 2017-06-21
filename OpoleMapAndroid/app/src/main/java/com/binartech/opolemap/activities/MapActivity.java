package com.binartech.opolemap.activities;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.binartech.opolemap.R;
import com.binartech.opolemap.fragments.PlacesListFragment;
import com.binartech.opolemap.fragments.TiledMapFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class MapActivity extends SherlockFragmentActivity
{

    private TiledMapFragment mMapFragment;
    private PlacesListFragment mPlacesFragment;
    private String category;
    private int poi;

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        category = getIntent().getStringExtra("category");
        poi = getIntent().getIntExtra("poi", 0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(category);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        if(getResources().getBoolean(R.bool.isTablet)) {
        	mPlacesFragment = (PlacesListFragment) getSupportFragmentManager().findFragmentById(R.id.places);
            if(mPlacesFragment != null) {
            	mPlacesFragment.setDisplayedCategory(getIntent().getStringExtra("category"));
            }
        }
        mMapFragment = (TiledMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (bundle == null)
        {
            mMapFragment.setDisplayedCategory(category, poi);
        } else {
        	mMapFragment.setDisplayedCategory(bundle.getString("category"), bundle.getInt("poi"));
        }
        if(!getResources().getBoolean(R.bool.isTablet))
        	overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        final boolean result = super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
            default:
                return result;
        }
    }
    
    @Override
	public void finish() 
    {
		super.finish();
		if(!getResources().getBoolean(R.bool.isTablet))
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	public void onBackPressed() 
	{
		super.onBackPressed();
		if(!getResources().getBoolean(R.bool.isTablet))
			overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) 
	{
		super.onSaveInstanceState(outState);
		outState.putString("category", category);
		outState.putInt("poi", poi);
	}
    
	
    
    
}
