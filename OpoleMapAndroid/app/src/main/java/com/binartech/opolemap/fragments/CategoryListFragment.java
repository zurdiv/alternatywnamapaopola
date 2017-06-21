package com.binartech.opolemap.fragments;

import com.actionbarsherlock.app.SherlockListFragment;
import com.binartech.opolemap.R;
import com.binartech.opolemap.activities.CategoriesActivity;
import com.binartech.opolemap.activities.MapActivity;
import com.binartech.opolemap.activities.PlacesActivity;
import com.binartech.opolemap.core.ObjectAdapter;
import com.binartech.opolemap.core.Poi;
import com.binartech.opolemap.core.PoiStore;
import com.binartech.opolemap.core.ObjectAdapter.ViewRenderer;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

public class CategoryListFragment extends SherlockListFragment implements ViewRenderer<String>
{

    private ObjectAdapter<String> mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAdapter = new ObjectAdapter<String>(this, true);
        mAdapter.setRange(PoiStore.loadFromCache(getActivity(), R.raw.pois).getCategories());
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        final String category = (String) getListAdapter().getItem(position);
        final PlacesListFragment places = (PlacesListFragment) getFragmentManager().findFragmentById(R.id.places);
        if ((places != null) && places.isInLayout())
        {
            places.setDisplayedCategory(category);
        }
        else
        {
        	Intent intent;
        	if(getResources().getBoolean(R.bool.isTablet)) {
        		intent = new Intent(getActivity(), MapActivity.class);
        		intent.putExtra("poi", 1);
        		PoiStore.setSelectedPoiIndex(0);
        		CategoriesActivity.category = category;
        	} else {
        		intent = new Intent(getActivity(), PlacesActivity.class);
        	}
            intent.putExtra("category", category);
            startActivity(intent);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        // Brazowy motyw
//        getListView().setDivider(new ColorDrawable(Color.BLACK));
//        getListView().setDividerHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics()));
    }

    @Override
    public View renderView(String item, int pos, View view, ViewGroup parent)
    {
        ImageView image;
        if (view == null)
        {
            view = getActivity().getLayoutInflater().inflate(R.layout.item_category, parent, false);
        }
        image = (ImageView) view.findViewById(R.id.image);
        image.setImageResource(PoiStore.getListDrawableForCategory(item));
        return view;
    }
}
