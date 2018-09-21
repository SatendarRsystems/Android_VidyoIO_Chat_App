package com.vidyo.io.demo.utilities;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import com.vidyo.io.demo.R;
import java.util.Random;

/**
 * Summary :Utility class to generator name badges
 * Description : This class is used to show the participant Name in different circle color
 * @author RSI
 * @date 20.08.2018
 */
public class NameBadgeUtil {
    private int[] androidColors;

    public NameBadgeUtil(Activity mActivity) {
        this.androidColors = mActivity.getResources().getIntArray(R.array.androidcolors);
    }

    /**
     * Generate circular name badge with first character of name
     */
    public Bitmap generateCircleBitmap(int circleColor, float diameterDP, String text) {
        final int textColor = 0xffffffff;

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float diameterPixels = diameterDP * (metrics.densityDpi / 160f);
        float radiusPixels = diameterPixels / 2;

        // Create the bitmap
        Bitmap output = Bitmap.createBitmap((int) diameterPixels, (int) diameterPixels, Bitmap.Config.ARGB_8888);

        // Create the canvas to draw on
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);

        // Draw the circle
        final Paint paintC = new Paint();
        paintC.setAntiAlias(true);
        paintC.setColor(androidColors[new Random().nextInt(androidColors.length)]);
        canvas.drawCircle(radiusPixels, radiusPixels, radiusPixels, paintC);

        // Draw the text
        if (text != null && text.length() > 0) {
            final Paint paintT = new Paint();
            paintT.setColor(textColor);
            paintT.setAntiAlias(true);
            paintT.setTextSize(radiusPixels);
            paintT.setTypeface(Typeface.SANS_SERIF);
            final Rect textBounds = new Rect();
            paintT.getTextBounds(text, 0, text.length(), textBounds);
            canvas.drawText(text, radiusPixels - textBounds.exactCenterX(), radiusPixels - textBounds.exactCenterY(), paintT);
        }

        return output;
    }
}
