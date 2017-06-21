package com.binartech.opolemap.fragments;

import java.util.Locale;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.binartech.opolemap.R;
import com.binartech.opolemap.core.Poi;
import com.binartech.opolemap.core.PoiStore;

public class PoiDetailsFragment extends SherlockDialogFragment implements OnClickListener
{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        return new Dialog(getActivity(), R.style.Opole_Theme_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_details, container, false);
        final ImageView marker = (ImageView) view.findViewById(R.id.image);
        final TextView number = (TextView) view.findViewById(R.id.marker_number);
        final TextView title = (TextView) view.findViewById(R.id.title);
        final TextView description = (TextView) view.findViewById(R.id.description);
        final View features = view.findViewById(R.id.features);
        final Poi poi = getArguments().getParcelable("poi");
        view.findViewById(R.id.button_ok).setOnClickListener(this);
        marker.setImageResource(PoiStore.getMarkerDrawableForCategory(poi.category));
        number.setText(String.format(Locale.US, "%02d", poi.index));
        title.setText(poi.title);
        description.setText(poi.description);
        evaluateFeatures(features, poi.featured);
        return view;
    }

    private void evaluateFeatures(View parent, int features)
    {
        boolean someoneVisible = false;
        for (int feature : Poi.getFeatures())
        {
            View view = parent.findViewById(PoiStore.getViewIdForFeature(feature));
            boolean passed = (feature & features) != 0;
            someoneVisible |= passed;
            view.setVisibility(passed ? View.VISIBLE : View.GONE);
            view.setOnClickListener(this);
        }
        if (!someoneVisible)
        {
            parent.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v instanceof Button)
        {
            dismiss();
        }
        else if (v instanceof ImageView)
        {
            Toast toast = Toast.makeText(getActivity(), PoiStore.getFeatureExplanationForViewId(v.getId()), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
