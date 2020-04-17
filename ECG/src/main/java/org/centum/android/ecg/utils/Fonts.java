package org.centum.android.ecg.utils;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by Phani on 5/28/2014.
 */
public class Fonts {

    public static final int FONT_ROBOTO_THIN = 0;
    public static final int FONT_ROBOTO_LIGHT = 1;

    private static String[] fontPaths = {
            "fonts/Roboto-Thin.ttf",
            "fonts/Roboto-Light.ttf"
    };

    private static Typeface typefaces[];
    private static boolean fontsLoaded = false;

    public static Typeface getTypeface(int font) {
        if (!fontsLoaded) {
            return Typeface.DEFAULT;
        }
        return typefaces[font];
    }

    public static void loadFonts(Context context) {
        typefaces = new Typeface[fontPaths.length];
        for (int i = 0; i < fontPaths.length; i++) {
            typefaces[i] = Typeface.createFromAsset(context.getAssets(), fontPaths[i]);
        }
        fontsLoaded = true;
    }

}
