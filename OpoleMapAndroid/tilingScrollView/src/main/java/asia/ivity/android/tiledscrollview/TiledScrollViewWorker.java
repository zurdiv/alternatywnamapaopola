/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * (c) 2011 Sebastian Roth <sebastian.roth@gmail.com>
 */

package asia.ivity.android.tiledscrollview;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import asia.ivity.android.tiledscrollview.TiledScrollView.ZoomLevel;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tiled Scroll View worker class that handles loading and display of the pictures.
 *
 * @author Sebastian Roth <sebastian.roth@gmail.com>
 */
public class TiledScrollViewWorker extends TwoDScrollView
{
    static final int FILL_TILES_DELAY = 200;

    private OnZoomLevelChangedListener onZoomLevelChangedListener = null;

    public TiledScrollViewWorker(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        readAttributes(attrs);
        resetContainers(true);
    }

    public TiledScrollViewWorker(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        readAttributes(attrs);
        resetContainers(true);
    }

    public TiledScrollViewWorker(Context context, ConfigurationSet defConfig)
    {
        super(context);
        readAttributes(defConfig);
        resetContainers(true);
    }

    private void readAttributes(ConfigurationSet configSet)
    {
        mConfigurationSets.put(TiledScrollView.ZoomLevel.DEFAULT, configSet);
    }

    private void readAttributes(AttributeSet attrs)
    {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.asia_ivity_android_tiledscrollview_TiledScrollView);

        int imageWidth = a.getInt(R.styleable.asia_ivity_android_tiledscrollview_TiledScrollView_image_width, -1);
        int imageHeight = a.getInt(R.styleable.asia_ivity_android_tiledscrollview_TiledScrollView_image_height, -1);
        int tileWidth = a.getInt(R.styleable.asia_ivity_android_tiledscrollview_TiledScrollView_tile_width, -1);
        int tileHeight = a.getInt(R.styleable.asia_ivity_android_tiledscrollview_TiledScrollView_tile_height, -1);
        String filePattern = a.getString(R.styleable.asia_ivity_android_tiledscrollview_TiledScrollView_file_pattern);

        // TODO: Move Validation to ConfigurationSet itself.
        if ((imageWidth == -1) || (imageHeight == -1) || (tileWidth == -1) || (tileHeight == -1) || (filePattern == null))
        {
            throw new IllegalArgumentException("Please set all attributes correctly!");
        }

        readAttributes(new ConfigurationSet(filePattern, tileWidth, tileHeight, imageWidth, imageHeight));
    }

    private void resetContainers(boolean fromInit)
    {
        mPendingLayout = false;
        mRootContainer = new FrameLayout(getContext());
        mTileContainer = new FrameLayout(getContext());
        mMarkerContainer = new FrameLayout(getContext());

        ConfigurationSet set = getCurrentConfigurationSet();

        final LayoutParams lp = new LayoutParams(set.getImageWidth(), set.getImageHeight());
        // Required?
        mRootContainer.setMinimumWidth(set.getImageWidth());
        mRootContainer.setMinimumHeight(set.getImageHeight());
        mRootContainer.setLayoutParams(lp);
        final LayoutParams lp1 = new LayoutParams(set.getImageWidth(), set.getImageHeight(), Gravity.TOP | Gravity.LEFT);
        final LayoutParams lp2 = new LayoutParams(set.getImageWidth(), set.getImageHeight(), Gravity.TOP | Gravity.LEFT);
        mTileContainer.setMinimumWidth(set.getImageWidth());
        mTileContainer.setMinimumHeight(set.getImageHeight());
        mMarkerContainer.setMinimumWidth(set.getImageWidth());
        mMarkerContainer.setMinimumHeight(set.getImageHeight());
        mRootContainer.addView(mTileContainer, lp1);
        mRootContainer.addView(mMarkerContainer, lp2);
        addView(mRootContainer, lp);
        refreshMarkers();
        setPendingAfterReset(fromInit);
    }

    private void setPendingAfterReset(final boolean fromInit)
    {
        mPendingLayout = true;
        mTileContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {

            @Override
            public void onGlobalLayout()
            {
                if (mTileContainer.getViewTreeObserver().isAlive())
                {
                    mTileContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                Boolean scrolled = null;
                if (mPendingScroll != null)
                {
                    scrollTo(mPendingScroll.x, mPendingScroll.y);
                    scrolled = getLastScrollStatus();
                    mPendingScroll = null;
                }
                if (mPendingCenter != null)
                {
                    scrollTo(mPendingCenter.x - (getWidth() / 2), mPendingCenter.y - (getHeight() / 2));
                    if ((scrolled == null) || !scrolled.booleanValue())
                    {
                        scrolled = getLastScrollStatus();
                    }
                    mPendingCenter = null;
                }
                if ((scrolled != null) && !scrolled.booleanValue())
                {
                    reloadTiles();
                }
                mPendingLayout = false;
            }
        });
    }

    @SuppressWarnings("rawtypes")
    private void changeZoomLevel(TiledScrollView.ZoomLevel next)
    {
        if ((next != mCurrentZoomLevel) && mConfigurationSets.containsKey(next))
        {
            // #ifdef DEBUG
            Log.i(TAG, "changeZoomLevel(" + next + ")");
            // #endif

            mHandler.removeCallbacks(mLoadTilesEvent);
            for (AsyncTask task : mRunningTasks)
            {
                task.cancel(true);
            }
            mRunningTasks.clear();
            mCurrentZoomLevel = next;
            tiles.clear();
            double w = mTileContainer.getWidth();
            double h = mTileContainer.getHeight();
            // #ifdef DEBUG
            Log.v(TAG, "Scroll X: " + getScrollX());
            Log.v(TAG, "Scroll Y: " + getScrollY());
            // #endif
            double viewPortCenterX = getScrollX() + (getWidth() / 2);
            double viewPortCenterY = getScrollY() + (getHeight() / 2);

            removeAllViews();
            resetContainers(false);

            double newW = getCurrentConfigurationSet().getImageWidth();
            double newH = getCurrentConfigurationSet().getImageHeight();

            Log.d(TAG, "Viewport: " + viewPortCenterX + "x" + viewPortCenterY);
            Log.d(TAG, "TileContainer: " + w + "x" + h);
            Log.d(TAG, "New image: " + newW + "x" + newH);

            int sX = (int) (((int) viewPortCenterX / w) * newW);
            int sY = (int) (((int) viewPortCenterY / h) * newH);
            sX -= (getWidth() / 2);
            sY -= (getHeight() / 2);
            Log.d(TAG, "New Viewport: " + sX + "x" + sY);
            mPendingScroll = new Point(sX, sY);

            if (onZoomLevelChangedListener != null)
            {
                onZoomLevelChangedListener.onZoomLevelChanged(mCurrentZoomLevel);
            }

        }
    }

    public void setOnZoomLevelChangedListener(OnZoomLevelChangedListener listener)
    {
        this.onZoomLevelChangedListener = listener;
    }

    TiledScrollView.ZoomLevel mCurrentZoomLevel = TiledScrollView.ZoomLevel.DEFAULT;

    Map<TiledScrollView.ZoomLevel, ConfigurationSet> mConfigurationSets = new HashMap<TiledScrollView.ZoomLevel, ConfigurationSet>();

    private ConfigurationSet getCurrentConfigurationSet()
    {
        if (mConfigurationSets.containsKey(mCurrentZoomLevel))
        {
            return mConfigurationSets.get(mCurrentZoomLevel);
        }

        return mConfigurationSets.get(TiledScrollView.ZoomLevel.DEFAULT);
    }

    public void addConfigurationSet(TiledScrollView.ZoomLevel level, ConfigurationSet set)
    {
        mConfigurationSets.put(level, set);
    }

    private FrameLayout mRootContainer;
    private FrameLayout mTileContainer;
    private FrameLayout mMarkerContainer;
    private static final String TAG = TiledScrollViewWorker.class.getSimpleName();
    // private float mDensity;
    private Handler mHandler = new Handler();

    private Map<Tile, SoftReference<ImageView>> tiles = new ConcurrentHashMap<Tile, SoftReference<ImageView>>();

    private final Runnable mLoadTilesEvent = new Runnable()
    {

        @Override
        public void run()
        {
            try
            {
                fillTiles();
            }
            catch (IOException e)
            {
                // #ifdef DEBUG
                Log.w(TAG, e);
                // #endif
            }
        }
    };

    private boolean mPendingLayout;
    private Point mPendingScroll;
    private Point mPendingCenter;
    private ZoomLevel mPendingZoom;

    public void setZoom(ZoomLevel level)
    {
        changeZoomLevel(level);
    }

    public void scrollLayerTo(int x, int y)
    {
        if (mPendingLayout)
        {
            mPendingScroll = new Point(x, y);
            mPendingCenter = null;
        }
        else
        {
            scrollTo(x, y);
        }
    }

    public void centerLayerTo(int x, int y)
    {
        if (mPendingLayout)
        {
            mPendingCenter = new Point(x, y);
            mPendingScroll = null;
        }
        else
        {
            scrollTo(x - (getWidth() / 2), y - (getHeight() / 2));
        }
    }

    public Point getMapCenter()
    {
        return new Point(getScrollX() + (getWidth() / 2), getScrollY() + (getHeight() / 2));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        try
        {
            // #ifdef DEBUG
            Log.v(TAG, "onSizeChanged(w: " + w + ", h: " + h + ')');
            // #endif
            fillTiles();
        }
        catch (IOException e)
        {
            // #ifdef DEBUG
            Log.w(TAG, e);
            // #endif
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt)
    {
        super.onScrollChanged(l, t, oldl, oldt);
        reloadTiles();
    }

    public void reloadTiles()
    {
        mHandler.removeCallbacks(mLoadTilesEvent);
        mHandler.postDelayed(mLoadTilesEvent, FILL_TILES_DELAY);
    }

    protected final ArrayList<AsyncTask> mRunningTasks = new ArrayList<AsyncTask>();

    private void fillTiles() throws IOException
    {
        mHandler.removeCallbacks(mLoadTilesEvent);
        mHandler.removeCallbacks(mCleanTilesEvent);
        Rect visible = new Rect();
        mTileContainer.getDrawingRect(visible);

        final int left = visible.left + getScrollX();
        final int top = visible.top + getScrollY();

        final ConfigurationSet set = getCurrentConfigurationSet();

        // Update the logic here. Sometimes, we don't need to add 1 tile to the right and bottom,
        // as it might be already exact. In that case, it's loading tiles that will be cleaned up
        // immediately in #cleanupTiles().
        final int width = (getMeasuredWidth()) + getScrollX() + set.getTileWidth();
        final int height = (getMeasuredHeight()) + getScrollY() + set.getTileHeight();
        mRunningTasks.add(new LoadTileTask(set, top, height, left, width).execute((Void[]) null));
    }

    private final Runnable mCleanTilesEvent = new Runnable()
    {

        @Override
        public void run()
        {
            cleanupOldTiles();
        }
    };

    private ImageView getNewTile(Tile tile) throws IOException
    {
        ImageView iv = new ImageView(getContext());

        ConfigurationSet set = getCurrentConfigurationSet();

        InputStream is;
        String path = set.getFilePattern().replace("%col%", new Integer(tile.y).toString()).replace("%row%", new Integer(tile.x).toString());
        try
        {
            is = getResources().getAssets().open(path);
            Bitmap bm = BitmapFactory.decodeStream(is);
            iv.setImageBitmap(bm);
            iv.setMinimumWidth(bm.getWidth());
            iv.setMinimumHeight(bm.getHeight());
            iv.setMaxWidth(bm.getWidth());
            iv.setMaxHeight(bm.getHeight());
            is.close();
        }
        catch (IOException e)
        {
            throw new IOException("Cannot open asset at:" + path);
        }

        iv.setTag(tile);

        return iv;
    }

    protected final class LoadTileTask extends AsyncTask<Void, ImageView, Void>
    {
        private final ConfigurationSet set;
        private final int top;
        private final int height;
        private final int left;
        private final int width;

        protected LoadTileTask(ConfigurationSet set, int top, int height, int left, int width)
        {
            this.set = set;
            this.top = top;
            this.height = height;
            this.left = left;
            this.width = width;
        }

        @Override
        protected void onCancelled()
        {
            super.onCancelled();
            for (Iterator<AsyncTask> iter = mRunningTasks.iterator(); iter.hasNext();)
            {
                if (iter.next() == this)
                {
                    iter.remove();
                }
            }
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            for (int y = top; (y < height) && !Thread.interrupted();)
            {
                final int tileY = new Double(Math.ceil(y / set.getTileHeight())).intValue();
                for (int x = left; x < width;)
                {
                    final int tileX = new Double(Math.ceil(x / set.getTileWidth())).intValue();

                    final Tile tile = new Tile(tileX, tileY);

                    if (!tiles.containsKey(tile) || (tiles.get(tile).get() == null))
                    {
                        try
                        {
                            publishProgress(getNewTile(tile));
                        }
                        catch (IOException e)
                        {
                            // Do nothing.
                        }
                    }
                    else
                    {
                    }

                    x = x + set.getTileWidth();
                }
                y = y + set.getTileHeight();
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(ImageView... ivs)
        {
            if (!isCancelled())
            {
                for (ImageView iv : ivs)
                {
                    if (iv == null)
                    {
                        continue;
                    }

                    final Tile tile = (Tile) iv.getTag();

                    iv.setId(new Random().nextInt());
                    FrameLayout.LayoutParams lp2 = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

                    lp2.leftMargin = tile.x * set.getTileWidth();
                    lp2.topMargin = tile.y * set.getTileHeight();
                    lp2.gravity = Gravity.TOP | Gravity.LEFT;
                    iv.setLayoutParams(lp2);

                    mTileContainer.addView(iv, lp2);

                    // Not yet functional.
                    // Log.d(TAG, "Animating: " + tile);
                    // iv.startAnimation(mFadeInAnimation);

                    tiles.put(tile, new SoftReference<ImageView>(iv));
                }
            }
        }

        @Override
        protected void onPostExecute(Void result)
        {
            super.onPostExecute(result);
            mHandler.postDelayed(mCleanTilesEvent, 2000);
        }

    }

    public void cleanupOldTiles()
    {
        Log.d(TAG, "Cleanup old tiles");

        Rect actualRect = new Rect(getScrollX(), getScrollY(), getWidth() + getScrollX(), getHeight() + getScrollY());

        for (Tile tile : tiles.keySet())
        {
            final ImageView v = tiles.get(tile).get();
            Rect r = new Rect();
            v.getHitRect(r);

            if (!Rect.intersects(actualRect, r))
            {
                if (v != null)
                {
                    final ViewGroup vg = (ViewGroup) v.getParent();
                    vg.removeView(v);
                    BitmapDrawable drawable = (BitmapDrawable) v.getDrawable();
                    if (drawable != null)
                    {
                        v.setImageDrawable(null);
                        drawable.setCallback(null);
                        Bitmap bmp = drawable.getBitmap();
                        if (bmp != null)
                        {
                            bmp.recycle();
                        }
                    }
                }
                tiles.remove(tile);
            }
        }
    }

    private boolean inZoomMode = false;
    private boolean ignoreLastFinger = false;
    private float mOrigSeparation;
    private static final float ZOOMJUMP = 75f;

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        int action = e.getAction() & MotionEvent.ACTION_MASK;
        if (e.getPointerCount() == 2)
        {
            inZoomMode = true;
        }
        else
        {
            inZoomMode = false;
        }
        if (inZoomMode)
        {
            switch (action)
            {
                case MotionEvent.ACTION_POINTER_DOWN:
                    // We may be starting a new pinch so get ready
                    mOrigSeparation = calculateSeparation(e);
                break;
                case MotionEvent.ACTION_POINTER_UP:
                    // We're ending a pinch so prepare to
                    // ignore the last finger while it's the
                    // only one still down.
                    ignoreLastFinger = true;
                break;
                case MotionEvent.ACTION_MOVE:
                    // We're in a pinch so decide if we need to change
                    // the zoom level.
                    float newSeparation = calculateSeparation(e);
                    TiledScrollView.ZoomLevel next = mCurrentZoomLevel;
                    if ((newSeparation - mOrigSeparation) > ZOOMJUMP)
                    {
                        Log.d(TAG, "Zoom In!");

                        next = mCurrentZoomLevel.upLevel();
                        mOrigSeparation = newSeparation;
                    }
                    else if ((mOrigSeparation - newSeparation) > ZOOMJUMP)
                    {
                        Log.d(TAG, "Zoom Out!");

                        next = mCurrentZoomLevel.downLevel();
                        mOrigSeparation = newSeparation;
                    }

                    changeZoomLevel(next);

                break;
            }
            // Don't pass these events to Android because we're
            // taking care of them.
            return true;
        }
        else
        {
            // cleanup if necessary from zooming logic
        }
        // Throw away events if we're on the last finger
        // until the last finger goes up.
        if (ignoreLastFinger)
        {
            if (action == MotionEvent.ACTION_UP)
            {
                ignoreLastFinger = false;
            }
            return true;
        }
        return super.onTouchEvent(e);
    }

    private float calculateSeparation(MotionEvent e)
    {
        float x = e.getX(0) - e.getX(1);
        float y = e.getY(0) - e.getY(1);
        return FloatMath.sqrt((x * x) + (y * y));
    }

    public boolean canZoomFurtherDown()
    {
        return (mCurrentZoomLevel.downLevel() != mCurrentZoomLevel) && mConfigurationSets.containsKey(mCurrentZoomLevel.downLevel());
    }

    public void zoomDown()
    {
        changeZoomLevel(mCurrentZoomLevel.downLevel());
    }

    public boolean canZoomFurtherUp()
    {
        return (mCurrentZoomLevel.upLevel() != mCurrentZoomLevel) && mConfigurationSets.containsKey(mCurrentZoomLevel.upLevel());
    }

    public void zoomUp()
    {
        changeZoomLevel(mCurrentZoomLevel.upLevel());
    }

    public static final class MarkerViewSpec
    {
        public static final int ALIGN_TOP = 1;
        public static final int ALIGN_LEFT = ALIGN_TOP << 1;
        public static final int ALIGN_H_CENTER = ALIGN_LEFT << 1;
        public static final int ALIGN_V_CENTER = ALIGN_H_CENTER << 1;
        public static final int ALIGN_RIGHT = ALIGN_V_CENTER << 1;
        public static final int ALIGN_BOTTOM = ALIGN_RIGHT << 1;
        public final View view;
        public final int align;
        private Point mExtraOffset;

        public MarkerViewSpec(View view, int align)
        {
            this.view = view;
            this.align = align;
        }

        public void setExtraOffset(Point pt)
        {
            this.mExtraOffset = pt;
        }

        public void setExtraOffset(int x, int y)
        {
            setExtraOffset(new Point(x, y));
        }

        public void setExtraOffsetDp(Context context, float x, float y)
        {
            final float fx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, x, context.getResources().getDisplayMetrics());
            final float fy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, y, context.getResources().getDisplayMetrics());
            setExtraOffset((int) fx, (int) fy);
        }

        protected Point measureView()
        {
            int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            int height = width;
            view.measure(width, height);
            return new Point(view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        private boolean isAlign(int align)
        {
            return (this.align & align) != 0;
        }

        protected void removeView()
        {
            final ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null)
            {
                viewGroup.removeView(view);
            }
        }

        protected View addToLayout(FrameLayout container, Point topLeft, int mapWidth, int mapHeight)
        {
            recomputePosition(topLeft, mapWidth, mapHeight);
            container.addView(view);
            return view;
        }

        public void recomputePosition(Point topLeft, int mapWidth, int mapHeight)
        {
            final Point measured = measureView();
            final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.TOP | Gravity.LEFT);
            if (isAlign(ALIGN_TOP))
            {
                params.topMargin = topLeft.y;
            }
            else if (isAlign(ALIGN_BOTTOM))
            {
                params.topMargin = topLeft.y - measured.y;
            }
            else if (isAlign(ALIGN_V_CENTER))
            {
                params.topMargin = topLeft.y - (measured.y / 2);
            }
            else
            {
                params.topMargin = topLeft.y;
            }
            if (isAlign(ALIGN_LEFT))
            {
                params.leftMargin = topLeft.x;
            }
            else if (isAlign(ALIGN_RIGHT))
            {
                params.leftMargin = topLeft.x - measured.x;
            }
            else if (isAlign(ALIGN_H_CENTER))
            {
                params.leftMargin = topLeft.x - (measured.x / 2);
            }
            else
            {
                params.leftMargin = topLeft.x;
            }
            if (mExtraOffset != null)
            {
                params.leftMargin += mExtraOffset.x;
                params.topMargin += mExtraOffset.y;
            }
            params.leftMargin = Math.max(0, Math.min(params.leftMargin, mapWidth - measured.x));
            params.topMargin = Math.max(0, Math.min(params.topMargin, mapHeight - measured.y));
            view.setLayoutParams(params);
        }
    }

    public static interface MarkerAdapter<T extends Marker>
    {
        public MarkerViewSpec getViewForMarker(T marker, int index, ViewGroup parent);
    }

    private MarkerAdapter mMarkerAdapter;

    public void setMarkerAdapter(MarkerAdapter adapter)
    {
        mMarkerAdapter = adapter;
    }

    private final ArrayList<Marker> mMarkerList = new ArrayList<Marker>();
    private final HashMap<Marker, MarkerViewSpec> mMarkerViews = new HashMap<Marker, MarkerViewSpec>();

    public void addMarker(Marker marker)
    {
        mMarkerList.add(marker);
        addMarkerToLayout(marker, mMarkerList.size() - 1);
    }

    public void setMarkers(List<? extends Marker> markers)
    {
        mMarkerList.clear();
        mMarkerList.addAll(markers);
        refreshMarkers();
    }

    private void addMarkerToLayout(Marker marker, int index)
    {
        if (mMarkerAdapter != null)
        {
            final Point position = marker.getPointForZoom(mCurrentZoomLevel);
            if (position != null)
            {
                final MarkerViewSpec spec = mMarkerAdapter.getViewForMarker(marker, index, mMarkerContainer);
                if (spec != null)
                {
                    final ConfigurationSet configSet = mConfigurationSets.get(mCurrentZoomLevel);
                    spec.addToLayout(mMarkerContainer, position, configSet.getImageWidth(), configSet.getImageHeight()); // zakładamy, że dimension mapy
                    mMarkerViews.put(marker, spec);
                }
            }
        }
    }

    public MarkerViewSpec getViewSpecForMarker(Marker marker)
    {
        return mMarkerViews.get(marker);
    }

    public ZoomLevel getZoomLevel()
    {
        return mCurrentZoomLevel;
    }

    public void refreshMarkers()
    {
        for (MarkerViewSpec spec : mMarkerViews.values())
        {
            spec.removeView();
        }
        mMarkerViews.clear();
        for (int i = 0, size = mMarkerList.size(); i < size; ++i)
        {
            addMarkerToLayout(mMarkerList.get(i), i);
        }
    }

    private void checkPendingActions()
    {
        if (mPendingCenter != null)
        {
            // #ifdef DEBUG
            Log.v(TAG, "pending center: size: " + getWidth() + "x" + getHeight());
            // #endif
            scrollTo(mPendingCenter.x - (getWidth() / 2), mPendingCenter.y - (getHeight() / 2));
        }
        else if (mPendingScroll != null)
        {
            // #ifdef DEBUG
            Log.v(TAG, "pending scroll");
            // #endif
            scrollTo(mPendingScroll.x, mPendingScroll.y);
        }
        mPendingScroll = null;
        mPendingCenter = null;
    }
}
