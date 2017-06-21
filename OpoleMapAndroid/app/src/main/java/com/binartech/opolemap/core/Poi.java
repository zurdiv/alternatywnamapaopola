package com.binartech.opolemap.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Poi implements Parcelable, Comparable<Poi>
{
    public final String category;
    public final String title;
    public final String description;
    public final int featured;
    public final double lat;
    public final double lng;
    public int index;
    public static final int ELECTRONIC_PAYMENT = 1;
    public static final int BICYCLE_PARKING = ELECTRONIC_PAYMENT << 1;
    public static final int BOARDS = BICYCLE_PARKING << 1; // planszówki = WTF?
    public static final int GARDENS = BOARDS << 1;
    public static final int KID_AWARE = GARDENS << 1;
    public static final int BOOK_CROSSING = KID_AWARE << 1;
    public static final int WIFI_ACCESS = BOOK_CROSSING << 1;
    public static final int DISABLED_PEOPLE_AWARE = WIFI_ACCESS << 1;

    public static Parcelable.Creator<Poi> CREATOR = new Creator<Poi>()
    {

        @Override
        public Poi[] newArray(int size)
        {
            return new Poi[size];
        }

        @Override
        public Poi createFromParcel(Parcel source)
        {
            return new Poi(source);
        }
    };

    protected Poi(Parcel source)
    {
        category = source.readString();
        title = source.readString();
        description = source.readString();
        featured = source.readInt();
        lat = source.readDouble();
        lng = source.readDouble();
        index = source.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(category);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(featured);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeInt(index);
    }

    public Poi(String[] values)
    {

        index = Integer.parseInt(values[0]);
        category = values[1].replace("\"", "");
        title = values[4].replace("\"", "");
        description = values[5].replace("\"", "");
        String fts = values[6].replace("\"", "");
        lat = Double.parseDouble(values[2]);
        lng = Double.parseDouble(values[3]);
        int mask = 0;
        if (!TextUtils.isEmpty(fts))
        {
            for (String str : fts.split(" "))
            {
                if (str.equalsIgnoreCase("pk"))
                {
                    mask |= ELECTRONIC_PAYMENT;
                }
                else if (str.equalsIgnoreCase("sr"))
                {
                    mask |= BICYCLE_PARKING;
                }
                else if (str.equalsIgnoreCase("pl"))
                {
                    mask |= BOARDS;
                }
                else if (str.equalsIgnoreCase("oo"))
                {
                    mask |= GARDENS;
                }
                else if (str.equalsIgnoreCase("pd"))
                {
                    mask |= KID_AWARE;
                }
                else if (str.equalsIgnoreCase("bc"))
                {
                    mask |= BOOK_CROSSING;
                }
                else if (str.equalsIgnoreCase("wf"))
                {
                    mask |= WIFI_ACCESS;
                }
                else if (str.equalsIgnoreCase("np"))
                {
                    mask |= DISABLED_PEOPLE_AWARE;
                }
            }
        }
        featured = mask;
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    public static int[] getFeatures()
    {
        return new int[] { ELECTRONIC_PAYMENT, BICYCLE_PARKING, BOARDS, GARDENS, KID_AWARE, BOOK_CROSSING, WIFI_ACCESS, DISABLED_PEOPLE_AWARE };
    }

	@Override
	public int compareTo(Poi arg0) {
		if(arg0.index < this.index) {
			return 1;
		} else if(arg0.index > this.index) {
			return -1;
		} else {
			return 0;
		}
	}

}
