package com.binartech.opolemap.core;

import java.util.EnumMap;

import com.binartech.opolemap.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class TypefaceCache
{
    // #ifdef DEBUG
//@    private static final String TAG = TypefaceCache.class.getSimpleName();
//@
    // #endif
    public static enum Font
    {
        LIBEL_SUIT("fonts/libelsuit.ttf"); //

        protected final String fontPath;
        protected static final Font[] FONTS = values();

        Font(String fontPath)
        {
            this.fontPath = fontPath;
        }
    }

    private static final EnumMap<Font, Typeface> sCached = new EnumMap<TypefaceCache.Font, Typeface>(Font.class);

    public static synchronized Typeface getFont(Context context, Font font)
    {
        Typeface face = sCached.get(font);
        if (face == null)
        {
            face = Typeface.createFromAsset(context.getAssets(), font.fontPath);
            sCached.put(font, face);
            // #ifdef DEBUG
//@            Log.d(TAG, "Loaded font: %s", font.fontPath);
            // #endif
        }
        return face;
    }

    public static Typeface getFont(Context context, AttributeSet attrs, boolean editMode, int defStyleAttr)
    {
        final TypedArray customTypeFace = context.obtainStyledAttributes(attrs, R.styleable.com_binartech_opolemap_core_TypefaceTextView, defStyleAttr, 0);
        try
        {
            if (!editMode)
            {
                final int index = customTypeFace.getInt(R.styleable.com_binartech_opolemap_core_TypefaceTextView_fontface, -1);
                if (index != -1)
                {
                    return getFont(context, Font.FONTS[index]);
                }
                else
                {
                    return null;
                }
            }
            return null;
        }
        finally
        {
            customTypeFace.recycle();
        }
    }
}
