package com.binartech.opolemap.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.FrameLayout;
import asia.ivity.android.tiledscrollview.TiledScrollView.ZoomLevel;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.binartech.opolemap.R;
import com.binartech.opolemap.fragments.PlacesListFragment;
import com.binartech.opolemap.fragments.TiledMapFragment;

public class PlacesActivity extends SherlockFragmentActivity
{

    private PlacesListFragment mPlaces;

    @Override
    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.activity_places);
        mPlaces = (PlacesListFragment) getSupportFragmentManager().findFragmentById(R.id.places);
        mPlaces.setDisplayedCategory(getIntent().getStringExtra("category"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

}
