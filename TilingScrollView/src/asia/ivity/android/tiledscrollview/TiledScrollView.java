package asia.ivity.android.tiledscrollview;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import asia.ivity.android.tiledscrollview.TiledScrollViewWorker.MarkerAdapter;

/**
 * Tiled scroll view main class.
 * <p/>
 * This class supports scrolling through a huge picture based on image tiles.
 * Most of the real "work" is done in the {@link TiledScrollViewWorker} class.
 *
 * @author Sebastian Roth <sebastian.roth@gmail.com>
 */
public class TiledScrollView extends FrameLayout implements OnZoomLevelChangedListener
{

    private TiledScrollViewWorker mScrollView;
    private ImageButton mBtnZoomDown;
    private ImageButton mBtnZoomUp;
    private boolean mZoomButtonsEnabled = true;

    public enum ZoomLevel
    {
        DEFAULT, LEVEL_1, LEVEL_2, LEVEL_3;

        public ZoomLevel upLevel()
        {
            switch (this)
            {
                case DEFAULT:
                    return LEVEL_1;
                case LEVEL_1:
                    return LEVEL_2;
                case LEVEL_2:
                    return LEVEL_3;
                case LEVEL_3:
                    return LEVEL_3;
            }

            return this;
        }

        public ZoomLevel downLevel()
        {
            switch (this)
            {
                case DEFAULT:
                    return DEFAULT;
                case LEVEL_1:
                    return DEFAULT;
                case LEVEL_2:
                    return LEVEL_1;
                case LEVEL_3:
                    return LEVEL_2;
            }

            return this;
        }
    }

    public TiledScrollView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init(attrs);
    }

    public TiledScrollView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);

        init(attrs);
    }

    public TiledScrollView(Context context, ConfigurationSet defConfig, boolean useZoomButtons)
    {
        super(context);
        init(new TiledScrollViewWorker(context, defConfig), useZoomButtons);
    }

    private void init(AttributeSet attrs)
    {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.asia_ivity_android_tiledscrollview_TiledScrollView);
        try
        {
            boolean zoomenabled = a.getBoolean(R.styleable.asia_ivity_android_tiledscrollview_TiledScrollView_zoom_buttons, true);
            init(new TiledScrollViewWorker(getContext(), attrs), zoomenabled);
        }
        finally
        {
            a.recycle();
        }
    }

    private void init(TiledScrollViewWorker worker, boolean useZoomButtons)
    {
        LayoutInflater lf = LayoutInflater.from(getContext());

        mScrollView = worker;
        mScrollView.setOnZoomLevelChangedListener(this);

        addView(mScrollView);

        mZoomButtonsEnabled = useZoomButtons;

        if (mZoomButtonsEnabled)
        {
            View btns = lf.inflate(R.layout.ll_tiledscroll_view_zoom_buttons, this, false);

            mBtnZoomDown = (ImageButton) btns.findViewById(R.id.btn_zoom_down);
            mBtnZoomUp = (ImageButton) btns.findViewById(R.id.btn_zoom_up);

            mBtnZoomDown.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mScrollView.zoomDown();
                }
            });
            mBtnZoomUp.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    mScrollView.zoomUp();
                }
            });

            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
            addView(btns, params);

            updateZoomButtons();
        }
    }

    public void addConfigurationSet(ZoomLevel level, ConfigurationSet set)
    {
        mScrollView.addConfigurationSet(level, set);

        updateZoomButtons();
    }

    private void updateZoomButtons()
    {
        if (mZoomButtonsEnabled)
        {
            if (!mScrollView.canZoomFurtherDown() && !mScrollView.canZoomFurtherUp())
            {
                mBtnZoomDown.setVisibility(GONE);
                mBtnZoomUp.setVisibility(GONE);
            }
            else
            {
                mBtnZoomDown.setVisibility(VISIBLE);
                mBtnZoomUp.setVisibility(VISIBLE);
                mBtnZoomDown.setEnabled(mScrollView.canZoomFurtherDown());
                mBtnZoomUp.setEnabled(mScrollView.canZoomFurtherUp());
            }
        }
    }

    public void cleanupOldTiles()
    {
        mScrollView.cleanupOldTiles();
    }

    public void setMarkerAdapter(MarkerAdapter adapter)
    {
        mScrollView.setMarkerAdapter(adapter);
    }

    public void addMarker(Marker marker)
    {
        mScrollView.addMarker(marker);
    }

    public void setMarkers(List<? extends Marker> markers)
    {
        mScrollView.setMarkers(markers);
    }

    @Override
    public void onZoomLevelChanged(ZoomLevel newLevel)
    {
        updateZoomButtons();
    }

    public void scrollLayerTo(int x, int y)
    {
        mScrollView.scrollLayerTo(x, y);
    }

    public void centerLayerTo(int x, int y)
    {
        mScrollView.centerLayerTo(x, y);
    }

    public void centerLayerTo(Point pt)
    {
        centerLayerTo(pt.x, pt.y);
    }

    public TiledScrollViewWorker getWorker()
    {
        return mScrollView;
    }
}
