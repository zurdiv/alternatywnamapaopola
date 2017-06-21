package asia.ivity.android.tiledscrollview;

import java.util.EnumMap;

import android.graphics.Point;
import asia.ivity.android.tiledscrollview.TiledScrollView.ZoomLevel;

public class Marker
{
    private final EnumMap<ZoomLevel, Point> mZoomForPoint = new EnumMap<TiledScrollView.ZoomLevel, Point>(ZoomLevel.class);

    public Marker(Point defaultPos)
    {
        mZoomForPoint.put(ZoomLevel.DEFAULT, defaultPos);
    }

    public void setPointForZoom(ZoomLevel level, Point point)
    {
        mZoomForPoint.put(level, point);
    }

    public Marker(int x, int y)
    {
        this(new Point(x, y));
    }

    public Point getPointForZoom(ZoomLevel level)
    {
        return mZoomForPoint.get(level);
    }
}