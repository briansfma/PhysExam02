package org.centum.android.ecg.utils.styledviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import org.centum.android.ecg.utils.Fonts;

/**
 * Created by Phani on 5/28/2014.
 */
public class RobotLightTextView extends TextView {
    public RobotLightTextView(Context context) {
        super(context);
        setTypeface(Fonts.getTypeface(Fonts.FONT_ROBOTO_LIGHT));
    }

    public RobotLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Fonts.getTypeface(Fonts.FONT_ROBOTO_LIGHT));
    }

    public RobotLightTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface(Fonts.getTypeface(Fonts.FONT_ROBOTO_LIGHT));
    }
}
