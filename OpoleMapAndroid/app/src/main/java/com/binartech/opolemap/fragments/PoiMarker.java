package com.binartech.opolemap.fragments;

import android.graphics.Point;
import asia.ivity.android.tiledscrollview.Marker;

import com.binartech.opolemap.core.Poi;

public class PoiMarker extends Marker
{
    public final Poi poi;

    public PoiMarker(Poi poi, Point defaultPos)
    {
        super(defaultPos);
        this.poi = poi;
    }

}