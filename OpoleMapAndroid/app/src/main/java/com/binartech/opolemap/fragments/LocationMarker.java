package com.binartech.opolemap.fragments;

import android.graphics.Point;
import asia.ivity.android.tiledscrollview.Marker;

public class LocationMarker extends Marker
{

    public LocationMarker(int x, int y)
    {
        super(x, y);
    }

    public LocationMarker(Point defaultPos)
    {
        super(defaultPos);

    }

}
