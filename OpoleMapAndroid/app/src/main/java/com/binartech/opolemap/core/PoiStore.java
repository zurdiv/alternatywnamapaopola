package com.binartech.opolemap.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.opencsv.CSVReader;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import com.binartech.opolemap.R;
import com.binartech.opolemap.activities.CategoriesActivity;

public class PoiStore extends HashMap<String, ArrayList<Poi>>
{
//	public static final String ALL_CATEGORIES = "Wszystkie";
    private static final HashMap<String, PoiStore> sCached = new HashMap<String, PoiStore>();
    private static final HashMap<String, Integer> sMarkers = new HashMap<String, Integer>();
    static
    {
        sMarkers.put("zrelaksuj się", R.drawable.marker_zrelaksuj_sie);
        sMarkers.put("posmakuj", R.drawable.marker_posmakuj);
        sMarkers.put("rozerwij się", R.drawable.marker_rozerwij_sie);
        sMarkers.put("odwiedź", R.drawable.marker_odwiedz);
        sMarkers.put("zobacz", R.drawable.marker_zobacz);
        sMarkers.put("stacja rowerowa", R.drawable.marker_stacja_rowerowa);
        // english
        sMarkers.put("relax", R.drawable.marker_zrelaksuj_sie);
        sMarkers.put("must taste", R.drawable.marker_posmakuj);
        sMarkers.put("have fun", R.drawable.marker_rozerwij_sie);
        sMarkers.put("must visit", R.drawable.marker_odwiedz);
        sMarkers.put("must see", R.drawable.marker_zobacz);
        sMarkers.put("bicycle station", R.drawable.marker_stacja_rowerowa);
        // german
        sMarkers.put("zum entspannen", R.drawable.marker_zrelaksuj_sie);
        sMarkers.put("etwas für die geschmacksknospen", R.drawable.marker_posmakuj);
        sMarkers.put("freizeit möglichkeiten", R.drawable.marker_rozerwij_sie);
        sMarkers.put("besuchenwertes", R.drawable.marker_odwiedz);
        sMarkers.put("sehenswertes", R.drawable.marker_zobacz);
        sMarkers.put("fahrradstation", R.drawable.marker_stacja_rowerowa);
    }
    private static final HashMap<String, Integer> sMarkersSelected = new HashMap<String, Integer>();
    static
    {
    	sMarkersSelected.put("zrelaksuj się", R.drawable.marker_zrelaksuj_sie_zaznaczony);
    	sMarkersSelected.put("posmakuj", R.drawable.marker_posmakuj_zaznaczony);
    	sMarkersSelected.put("rozerwij się", R.drawable.marker_rozerwij_sie_zaznaczony);
    	sMarkersSelected.put("odwiedź", R.drawable.marker_odwiedz_zaznaczony);
    	sMarkersSelected.put("zobacz", R.drawable.marker_zobacz_zaznaczony);
    	sMarkersSelected.put("stacja rowerowa", R.drawable.marker_stacja_rowerowa_zaznaczony);
        // english
    	sMarkersSelected.put("relax", R.drawable.marker_zrelaksuj_sie_zaznaczony);
    	sMarkersSelected.put("must taste", R.drawable.marker_posmakuj_zaznaczony);
    	sMarkersSelected.put("have fun", R.drawable.marker_rozerwij_sie_zaznaczony);
    	sMarkersSelected.put("must visit", R.drawable.marker_odwiedz_zaznaczony);
    	sMarkersSelected.put("must see", R.drawable.marker_zobacz_zaznaczony);
    	sMarkersSelected.put("bicycle station", R.drawable.marker_stacja_rowerowa_zaznaczony);
    	// german
    	sMarkersSelected.put("zum entspannen", R.drawable.marker_zrelaksuj_sie_zaznaczony);
    	sMarkersSelected.put("etwas für die geschmacksknospen", R.drawable.marker_posmakuj_zaznaczony);
    	sMarkersSelected.put("freizeit möglichkeiten", R.drawable.marker_rozerwij_sie_zaznaczony);
    	sMarkersSelected.put("besuchenwertes", R.drawable.marker_odwiedz_zaznaczony);
    	sMarkersSelected.put("sehenswertes", R.drawable.marker_zobacz_zaznaczony);
    	sMarkersSelected.put("fahrradstation", R.drawable.marker_stacja_rowerowa_zaznaczony);
    }

    private static final HashMap<String, Integer> sCategoryImages = new HashMap<String, Integer>();
    static
    {
        sCategoryImages.put("zrelaksuj się", R.drawable.zrelaksuj_sie);
        sCategoryImages.put("posmakuj", R.drawable.posmakuj);
        sCategoryImages.put("rozerwij się", R.drawable.rozerwij_sie);
        sCategoryImages.put("odwiedź", R.drawable.odwiedz);
        sCategoryImages.put("zobacz", R.drawable.zobacz);
        sCategoryImages.put("stacja rowerowa", R.drawable.stacje_rowerowe);
        sCategoryImages.put("wszystkie", R.drawable.wszystkie);
        // english
        sCategoryImages.put("relax", R.drawable.zrelaksuj_sie);
        sCategoryImages.put("must taste", R.drawable.posmakuj);
        sCategoryImages.put("have fun", R.drawable.rozerwij_sie);
        sCategoryImages.put("must visit", R.drawable.odwiedz);
        sCategoryImages.put("must see", R.drawable.zobacz);
        sCategoryImages.put("bicycle station", R.drawable.stacje_rowerowe);
        sCategoryImages.put("all locations", R.drawable.wszystkie);
        // german
        sCategoryImages.put("zum entspannen", R.drawable.zrelaksuj_sie);
        sCategoryImages.put("etwas für die geschmacksknospen", R.drawable.posmakuj);
        sCategoryImages.put("freizeit möglichkeiten", R.drawable.rozerwij_sie);
        sCategoryImages.put("besuchenwertes", R.drawable.odwiedz);
        sCategoryImages.put("sehenswertes", R.drawable.zobacz);
        sCategoryImages.put("fahrradstation", R.drawable.stacje_rowerowe);
        sCategoryImages.put("alle standpunkte", R.drawable.wszystkie);
        //Wspolne
        //Log.d("TAG", "Wszystkie = " + getCategoryAll());
        //sCategoryImages.put(getCategoryAll(), R.drawable.wszystkie);
    }

    private static final SparseIntArray sFeatureToViewId = new SparseIntArray();
    static
    {
        sFeatureToViewId.put(Poi.BICYCLE_PARKING, R.id.feature_bicycle_parking);
        sFeatureToViewId.put(Poi.BOARDS, R.id.feature_boards);
        sFeatureToViewId.put(Poi.BOOK_CROSSING, R.id.feature_book_crossing);
        sFeatureToViewId.put(Poi.DISABLED_PEOPLE_AWARE, R.id.feature_disabled_people_aware);
        sFeatureToViewId.put(Poi.ELECTRONIC_PAYMENT, R.id.feature_electronic_payment);
        sFeatureToViewId.put(Poi.GARDENS, R.id.feature_gardens);
        sFeatureToViewId.put(Poi.KID_AWARE, R.id.feature_kid_aware);
        sFeatureToViewId.put(Poi.WIFI_ACCESS, R.id.feature_wifi_access);
    }

    private static final SparseIntArray sViewIdToFeatureExplanation = new SparseIntArray();
    static
    {
        sViewIdToFeatureExplanation.put(R.id.feature_bicycle_parking, R.string.feature_bicycle_parking);
        sViewIdToFeatureExplanation.put(R.id.feature_boards, R.string.feature_board);
        sViewIdToFeatureExplanation.put(R.id.feature_book_crossing, R.string.feature_book_crossing);
        sViewIdToFeatureExplanation.put(R.id.feature_disabled_people_aware, R.string.feature_disabled_people_aware);
        sViewIdToFeatureExplanation.put(R.id.feature_electronic_payment, R.string.feature_electronic_payment);
        sViewIdToFeatureExplanation.put(R.id.feature_gardens, R.string.feature_garden);
        sViewIdToFeatureExplanation.put(R.id.feature_kid_aware, R.string.feature_kid_aware);
        sViewIdToFeatureExplanation.put(R.id.feature_wifi_access, R.string.feature_wifi);
    }

    /**
     * 
     */
    private static final long serialVersionUID = 9177342279387047289L;
    private static int selectedPoi = -1;
    
    public static int getSelectedPoiIndex() 
    {
    	return selectedPoi;
    }
    
    public static void setSelectedPoiIndex(int index) 
    {
    	selectedPoi = index;
    }

    public static int getMarkerDrawableForCategory(String category)
    {
        return sMarkers.get(category.toLowerCase()).intValue();
    }
    
    public static int getSelectedMarkerDrawableForCategory(String category)
    {
    	return sMarkersSelected.get(category.toLowerCase()).intValue();
    }
    
    public static int getListDrawableForCategory(String category)
    {
   	Log.d("TAG", category);
        return sCategoryImages.get(category.toLowerCase()).intValue();
    }

    public static int getViewIdForFeature(int feature)
    {
        return sFeatureToViewId.get(feature);
    }

    public static int getFeatureExplanationForViewId(int viewId)
    {
        return sViewIdToFeatureExplanation.get(viewId);
    }

    public List<String> getCategories()
    {
        final ArrayList<Entry<String, ArrayList<Poi>>> entries = new ArrayList<Entry<String, ArrayList<Poi>>>(entrySet());
        Collections.sort(entries, CATEGORY_COMPARATOR);
        final ArrayList<String> out = new ArrayList<String>(entries.size());
        for (Entry<String, ArrayList<Poi>> entry : entries)
        {
            out.add(entry.getKey());
        }
        String all = getCategoryAll();
//        Log.d("TAG", "ALL " + getCategoryAll());
        out.add(all);
        return out;
    }
    
    public static String getCategoryAll() {
    	//return Resources.getSystem().getString(R.string.wszystkie);
    	return App.getContext().getResources().getString(R.string.wszystkie);
    }

    private static final Comparator<Entry<String, ArrayList<Poi>>> CATEGORY_COMPARATOR = new Comparator<Entry<String, ArrayList<Poi>>>()
    {
        @Override
        public int compare(Entry<String, ArrayList<Poi>> lhs, Entry<String, ArrayList<Poi>> rhs)
        {
            final int lindex = lhs.getValue().get(0).index;
            final int rindex = rhs.getValue().get(0).index;
            if (lindex > rindex)
            {
                return 1;
            }
            else if (rindex > lindex)
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    };
    
    @Override
	public ArrayList<Poi> get(Object key) 
	{
    	ArrayList<Poi> retVal = new ArrayList<Poi>();
    	if(key != null && !key.equals(getCategoryAll()))
    		retVal = super.get(key);
    	else 
    	{
    		for(ArrayList<Poi> value : this.values()) 
    			retVal.addAll(value);
    		Collections.sort(retVal);
    	}
    	return retVal;
	}

	public List<Poi> getPlacesForCategory(String category)
    {
        final ArrayList<Poi> outPlaces = new ArrayList<Poi>();
        final ArrayList<Poi> places = get(category);
        if ((places != null) && !places.isEmpty())
        {
        	int i=1;
            for(Poi place : places) {
            	place.index = i++;
            }
            outPlaces.addAll(places);
        }
        return outPlaces;
    }

    public static PoiStore getDefault(Context context)
    {
        return loadFromCache(context, R.raw.pois);
    }

    private static String buildResourceName(int resource, Configuration configuration)
    {
        final long resLong = resource & 0xFFFFFFFFL;
        return String.format(Locale.US, "%d-%s", resLong, configuration.locale.getLanguage());
    }

    public static synchronized PoiStore loadFromCache(Context context, int resource)
    {
        final String buildResourceName = buildResourceName(resource, context.getResources().getConfiguration());
        PoiStore record = sCached.get(buildResourceName);
        if (record == null)
        {
            record = loadFromResource(context, resource);
        }
        sCached.put(buildResourceName, record);
        return record;
    }

    private static PoiStore loadFromResource(Context context, int resource)
    {
        InputStream stream = null;
        try
        {
            stream = context.getResources().openRawResource(resource);
            return loadFromStream(stream);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (stream != null)
            {
                try
                {
                    stream.close();
                }
                catch (Exception e2)
                {

                }
            }
        }
    }

    private static PoiStore loadFromStream(InputStream stream) throws IOException
    {
        final PoiStore result = new PoiStore();
        CSVReader reader = new CSVReader(new InputStreamReader(stream));
        String[] nextLine = null;
        try
        {

            while ((nextLine = reader.readNext()) != null)
            {
                final Poi poi = new Poi(nextLine);
                ArrayList<Poi> category = result.get(poi.category);
                if (category == null)
                {
                    category = new ArrayList<Poi>();
                    result.put(poi.category, category);
                }
                category.add(poi);
            }
        }
        catch (RuntimeException e)
        {
            System.out.println(Arrays.toString(nextLine));
            throw e;
        }
        return result;
    }
}
