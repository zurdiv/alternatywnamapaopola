package com.binartech.opolemap.fragments;

import java.util.Locale;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.binartech.opolemap.R;
import com.binartech.opolemap.activities.CategoriesActivity;
import com.binartech.opolemap.activities.MapActivity;
import com.binartech.opolemap.core.ObjectAdapter;
import com.binartech.opolemap.core.Poi;
import com.binartech.opolemap.core.PoiStore;
import com.binartech.opolemap.core.ObjectAdapter.ViewRenderer;
import com.binartech.opolemap.core.TypefaceCache;
import com.binartech.opolemap.core.TypefaceCache.Font;

public class PlacesListFragment extends SherlockListFragment implements ViewRenderer<Poi>
{

    private String category;
    private ObjectAdapter<Poi> mAdapter;

    public void setDisplayedCategory(String category)
    {
        this.category = category;
        mAdapter = new ObjectAdapter<Poi>(this, true);
        mAdapter.setRange(PoiStore.getDefault(getActivity()).getPlacesForCategory(category));
        setListAdapter(mAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //Brazowy motyw
//        getListView().setDivider(new ColorDrawable(Color.BLACK));
//        getListView().setDividerHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, getResources().getDisplayMetrics()));
        setListShown(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        final Intent intent = new Intent(getActivity(), MapActivity.class);
        intent.putExtra("category", category);
        PoiStore.setSelectedPoiIndex(position);
        intent.putExtra("poi", position);
        startActivity(intent);
        if(getResources().getBoolean(R.bool.isTablet)) {
        		CategoriesActivity.poi = position;
        		CategoriesActivity.category = category;
        		getActivity().finish();
        }
    }

    @Override
    public View renderView(Poi item, int pos, View view, ViewGroup parent)
    {
        final TextView textView;
        if (view == null)
        {
            view = getActivity().getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        textView = (TextView) view;
        textView.setTypeface(TypefaceCache.getFont(getActivity(), Font.LIBEL_SUIT));
        //Brazowy motyw
//        textView.setTextColor(Color.BLACK);
        //Czerwony motyw
        textView.setTextColor(Color.WHITE);
        textView.setText(String.format(Locale.US, "%02d. %s", item.index, item.title));
        return view;
    }

}
