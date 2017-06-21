package com.binartech.opolemap.fragments;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.binartech.opolemap.R;
import com.binartech.opolemap.core.MapUtils;
import com.binartech.opolemap.core.MapUtils.Projection;
import com.binartech.opolemap.core.Poi;
import com.binartech.opolemap.core.PoiStore;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import asia.ivity.android.tiledscrollview.ConfigurationSet;
import asia.ivity.android.tiledscrollview.Marker;
import asia.ivity.android.tiledscrollview.TiledScrollView;
import asia.ivity.android.tiledscrollview.TiledScrollView.ZoomLevel;
import asia.ivity.android.tiledscrollview.TiledScrollViewWorker.MarkerAdapter;
import asia.ivity.android.tiledscrollview.TiledScrollViewWorker.MarkerViewSpec;

public class TiledMapFragment extends SherlockFragment implements MarkerAdapter<Marker>, View.OnClickListener
{
    // #ifdef DEBUG
//@    private static final String TAG = TiledMapFragment.class.getSimpleName();
//@
    // #endif
    public static class MapConfiguration
    {
        public final Projection projection;
        public final ConfigurationSet configSet;

        protected MapConfiguration(Projection projection, ConfigurationSet configSet)
        {
            this.projection = projection;
            this.configSet = configSet;
        }
    }

    private String mCategory;
    private final EnumMap<ZoomLevel, MapConfiguration> mConfigurations = new EnumMap<TiledScrollView.ZoomLevel, TiledMapFragment.MapConfiguration>(ZoomLevel.class);

    private Location mLastLocation;

    private final LocationListener mNetworkListener = new LocationListener()
    {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

        @Override
        public void onProviderDisabled(String provider)
        {
        }

        @Override
        public void onLocationChanged(Location location)
        {
            updateCurrentLocation(location, false);
        }
    };

    private final LocationListener mGpsListener = new LocationListener()
    {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
        }

        @Override
        public void onProviderEnabled(String provider)
        {
        }

        @Override
        public void onProviderDisabled(String provider)
        {
        }

        @Override
        public void onLocationChanged(Location location)
        {
            updateCurrentLocation(location, true);
        }
    };

    public TiledMapFragment()
    {
        mConfigurations.put(ZoomLevel.DEFAULT, new MapConfiguration(new Projection(502), new ConfigurationSet("map/map_zoom1/map_tile_%row%_%col%.png", 200, 200, 502, 502)));
        mConfigurations.put(ZoomLevel.LEVEL_1, new MapConfiguration(new Projection(1004), new ConfigurationSet("map/map_zoom2/map_tile_%row%_%col%.png", 200, 200, 1004, 1004)));
        mConfigurations.put(ZoomLevel.LEVEL_2, new MapConfiguration(new Projection(2008), new ConfigurationSet("map/map_zoom3/map_tile_%row%_%col%.png", 200, 200, 2008, 2008)));
        mConfigurations.put(ZoomLevel.LEVEL_3, new MapConfiguration(new Projection(4016), new ConfigurationSet("map/map_zoom4/map_tile_%row%_%col%.png", 200, 200, 4016, 4016)));
    }

    private TiledScrollView mMap;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final TiledScrollView tsv = new TiledScrollView(getActivity(), mConfigurations.get(ZoomLevel.DEFAULT).configSet, true);
        for (Entry<ZoomLevel, MapConfiguration> entry : mConfigurations.entrySet())
        {
            if (entry.getKey() != ZoomLevel.DEFAULT)
            {
                tsv.addConfigurationSet(entry.getKey(), entry.getValue().configSet);
            }
        }
        tsv.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        tsv.setMarkerAdapter(this);
        mMap = tsv;
        return tsv;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null)
        {
            loadCategory(savedInstanceState.getString("category"));
            mMap.getWorker().setZoom(ZoomLevel.valueOf(savedInstanceState.getString("zoom")));
            mMap.getWorker().centerLayerTo(savedInstanceState.getInt("scroll_x"), savedInstanceState.getInt("scroll_y"));
            mLastLocation = savedInstanceState.getParcelable("location");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.map_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        final boolean result = super.onOptionsItemSelected(item);
        switch (item.getItemId())
        {
            case R.id.menu_my_location:
                if (mLastLocation != null)
                {
                    if (MapUtils.isInMapBounds(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                    {
                        mMap.centerLayerTo(mConfigurations.get(mMap.getWorker().getZoomLevel()).projection.toPixels(mLastLocation, null));
                    }
                    else
                    {
                        Toast toast = Toast.makeText(getActivity(), R.string.location_outside_map, Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }
                else
                {
                    Toast toast = Toast.makeText(getActivity(), R.string.location_unavailable, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
                return true;
            default:
                return result;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putString("zoom", mMap.getWorker().getZoomLevel().toString());
        final Point mapCenter = mMap.getWorker().getMapCenter();
        outState.putInt("scroll_x", mapCenter.x);
        outState.putInt("scroll_y", mapCenter.y);
        outState.putString("category", mCategory);
        outState.putParcelable("location", mLastLocation);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        LocationManager lman = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        lman.removeUpdates(mGpsListener);
        lman.removeUpdates(mNetworkListener);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        final Criteria gps = new Criteria();
        gps.setAccuracy(Criteria.ACCURACY_FINE);
        gps.setPowerRequirement(Criteria.POWER_HIGH);
        final Criteria network = new Criteria();
        network.setAccuracy(Criteria.ACCURACY_COARSE);
        network.setPowerRequirement(Criteria.POWER_LOW);
        network.setCostAllowed(true);
        LocationManager lman = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        String provider = lman.getBestProvider(gps, false);
        if (!TextUtils.isEmpty(provider))
        {
            lman.requestLocationUpdates(provider, 1000, 0, mGpsListener);
        }
        provider = lman.getBestProvider(network, false);
        if (!TextUtils.isEmpty(provider))
        {
            lman.requestLocationUpdates(provider, 10000, 300, mNetworkListener);
        }
        updateMapLocation();
    }

    @Override
    public MarkerViewSpec getViewForMarker(Marker src, int index, ViewGroup parent)
    {
        if (src instanceof PoiMarker)
        {
            PoiMarker marker = (PoiMarker) src;
            View view = getActivity().getLayoutInflater().inflate(R.layout.item_marker, parent, false);
            final ImageView image = (ImageView) view.findViewById(R.id.image);
            final TextView text = (TextView) view.findViewById(R.id.marker_number);
            if(PoiStore.getSelectedPoiIndex() == index) 
            	image.setImageResource(PoiStore.getSelectedMarkerDrawableForCategory(marker.poi.category));
            else
            	image.setImageResource(PoiStore.getMarkerDrawableForCategory(marker.poi.category));
            text.setText(String.format(Locale.US, "%02d", marker.poi.index));
            view.setTag(marker);
            view.setOnClickListener(this);
            final MarkerViewSpec spec = new MarkerViewSpec(view, MarkerViewSpec.ALIGN_BOTTOM | MarkerViewSpec.ALIGN_LEFT);
            spec.setExtraOffsetDp(getActivity(), -12f, 0f);
            return spec;
        }
        else if (src instanceof LocationMarker)
        {
            LocationMarker marker = (LocationMarker) src;
            ImageView image = new ImageView(getActivity());
            image.setImageResource(R.drawable.my_location);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            image.setLayoutParams(lp);
            return new MarkerViewSpec(image, MarkerViewSpec.ALIGN_V_CENTER | MarkerViewSpec.ALIGN_H_CENTER);
        }
        else
        {
            return null;
        }
    }

    protected void updateCurrentLocation(Location loc, boolean fromGps)
    {
        if ((mLastLocation == null) || fromGps || LocationManager.NETWORK_PROVIDER.equals(mLastLocation.getProvider()))
        {
            mLastLocation = loc;
            updateMapLocation();
        }
    }

    private LocationMarker mLocationMarkerInstance;

    private void updateMapLocation()
    {
        if (mLastLocation != null)
        {
            // #ifdef DEBUG
//@            Log.v(TAG, "updateMapLocation()");
            // #endif
            MarkerViewSpec spec;
            if ((mLocationMarkerInstance == null) || (((spec = mMap.getWorker().getViewSpecForMarker(mLocationMarkerInstance))) == null))
            {
                LocationMarker marker = createMarkerForLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                mMap.addMarker(marker);
                mLocationMarkerInstance = marker;
            }
            else
            {
                final Projection projection = mConfigurations.get(mMap.getWorker().getZoomLevel()).projection;
                spec.recomputePosition(projection.toPixels(mLastLocation, null), projection.getMapDimension(), projection.getMapDimension());
            }
        }
    }

    public LocationMarker createMarkerForLocation(double lat, double lng)
    {
        final LocationMarker marker = new LocationMarker(mConfigurations.get(ZoomLevel.DEFAULT).projection.toPixels(lat, lng, null));
        for (Entry<ZoomLevel, MapConfiguration> entry : mConfigurations.entrySet())
        {
            if (entry.getKey() != ZoomLevel.DEFAULT)
            {
                marker.setPointForZoom(entry.getKey(), entry.getValue().projection.toPixels(lat, lng, null));
            }
        }
        return marker;
    }

    public PoiMarker createPoiMarker(Poi poi)
    {
        final PoiMarker marker = new PoiMarker(poi, mConfigurations.get(ZoomLevel.DEFAULT).projection.toPixels(poi.lat, poi.lng, null));
        for (Entry<ZoomLevel, MapConfiguration> entry : mConfigurations.entrySet())
        {
            if (entry.getKey() != ZoomLevel.DEFAULT)
            {
                marker.setPointForZoom(entry.getKey(), entry.getValue().projection.toPixels(poi.lat, poi.lng, null));
            }
        }
        return marker;
    }

    public void setDisplayedCategory(String category, int poi)
    {
        final ArrayList<PoiMarker> markers = loadCategory(category);
        mMap.getWorker().setZoom(ZoomLevel.LEVEL_2);
        final PoiMarker marker = markers.get(poi);
        // if (MapUtils.isInMapBounds(marker.poi.lat, marker.poi.lng))
        {
            mMap.centerLayerTo(marker.getPointForZoom(mMap.getWorker().getZoomLevel()));
        }
//        else
//        {
//            mMap.centerLayerTo(mConfigurations.get(mMap.getWorker().getZoomLevel()).projection.toPixels(MapUtils.MAP_CENTER, null));
//        }
    }

    private ArrayList<PoiMarker> loadCategory(String category)
    {
        mCategory = category;
        final ArrayList<PoiMarker> markers = new ArrayList<PoiMarker>();
        final List<Poi> pois = PoiStore.getDefault(getActivity()).get(category);
        if (pois != null)
        {
            for (Poi poiInstance : pois)
            {
                final PoiMarker pmarker = createPoiMarker(poiInstance);
//                if (!MapUtils.isInMapBounds(poiInstance.lat, poiInstance.lng))
//                {
//
//                }
                markers.add(pmarker);
            }
        }
        mMap.setMarkers(markers);
        return markers;
    }

    @Override
    public void onClick(View v)
    {
        final Object tag = v.getTag();
        if (tag instanceof PoiMarker)
        {
            final PoiMarker poiMarker = (PoiMarker) tag;
            final Poi poi = poiMarker.poi;
            if (!TextUtils.isEmpty(poi.description))
            {
                Bundle bundle = new Bundle();
                bundle.putParcelable("poi", poi);
                final PoiDetailsFragment dlg = new PoiDetailsFragment();
                dlg.setArguments(bundle);
                dlg.show(getFragmentManager(), "info");
            }
            else
            {
                final Toast toast = Toast.makeText(getActivity(), String.format("%s: %s", poi.category, poi.title), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }
    }
    
    public void setZoom(ZoomLevel level) {
    	mMap.getWorker().setZoom(level);
    }

}
