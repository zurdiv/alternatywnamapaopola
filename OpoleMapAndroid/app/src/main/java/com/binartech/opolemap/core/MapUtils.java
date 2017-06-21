package com.binartech.opolemap.core;

import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;

public class MapUtils
{
//    private static final double[] BOTTOM_LEFT = { 50.6566, 17.9115 };
//    private static final double[] TOP_RIGHT = { 50.6758, 17.9408 };
    private static final double[] BOTTOM_LEFT = { 50.6562561316, 17.9093041441 };
    private static final double[] TOP_RIGHT = { 50.6760705494, 17.9415344207 };
    private static final double[] TOP_LEFT = { TOP_RIGHT[0], BOTTOM_LEFT[1] };
    private static final double[] BOTTOM_RIGHT = { BOTTOM_LEFT[0], TOP_RIGHT[1] };
    private static final int BASE_MAP_DIMENSION = 4016;

    public static final Location MAP_CENTER;
    static
    {
        MAP_CENTER = new Location(LocationManager.GPS_PROVIDER);
        MAP_CENTER.setLatitude(TOP_LEFT[0] - ((TOP_LEFT[0] - BOTTOM_LEFT[0]) / 2));
        MAP_CENTER.setLongitude(TOP_LEFT[1] + ((TOP_RIGHT[1] - TOP_LEFT[1]) / 2));
    }

    public static boolean isInMapBounds(double lat, double lng)
    {
        return (lat <= TOP_LEFT[0]) && (lat >= BOTTOM_LEFT[0]) && (lng >= TOP_LEFT[1]) && (lng <= TOP_RIGHT[1]);
    }

    public static class Projection
    {
        private final double xRatio;
        private final double yRatio;
        private final int dimension;

        public Projection(int mapDimension)
        {
            final double xdegree = TOP_RIGHT[1] - TOP_LEFT[1];
            final double ydegree = TOP_LEFT[0] - BOTTOM_LEFT[0];
            xRatio = mapDimension / xdegree;
            yRatio = mapDimension / ydegree;
            this.dimension = mapDimension;
        }

        public int getMapDimension()
        {
            return dimension;
        }

        public Point toPixels(double lat, double lng, Point out)
        {
            if (out == null)
            {
                out = new Point();
            }
            final double xdiff = lng - TOP_LEFT[1];
            final double ydiff = TOP_LEFT[0] - lat;
            out.x = Math.max(0, Math.min((int) (xdiff * xRatio), dimension)); // clamping
            out.y = Math.max(0, Math.min((int) (ydiff * yRatio), dimension));
            return out;
        }

        public Location toLocation(int x, int y, Location out)
        {
            if (out == null)
            {
                out = new Location(LocationManager.GPS_PROVIDER);
            }
            out.setLatitude((x / xRatio) + TOP_LEFT[0]);
            out.setLongitude((y / yRatio) + TOP_LEFT[1]);
            return out;
        }

        public Point toPixels(Location loc, Point out)
        {
            return toPixels(loc.getLatitude(), loc.getLongitude(), out);
        }
    }
}
