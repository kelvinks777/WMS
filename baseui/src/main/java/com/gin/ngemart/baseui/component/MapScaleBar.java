package com.gin.ngemart.baseui.component;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.location.Location;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.gin.ngemart.baseui.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.LatLng;

import java.util.Locale;


/**
 * Created by deli on 29/3/18.
 */

public class MapScaleBar extends AppCompatImageView{

    public enum ColorMode {
        COLOR_MODE_AUTO,
        COLOR_MODE_DARK,
        COLOR_MODE_WHITE
    }

    private static final int FT_IN_MILE = 5280;

    private static final int[] METERS = {1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 5000,
            10000, 20000, 50000, 100000, 200000, 500000, 1000000, 2000000};

    private static final int[] FT = {1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000,
            FT_IN_MILE, 2 * FT_IN_MILE, 5 * FT_IN_MILE, 10 * FT_IN_MILE, 20 * FT_IN_MILE, 50 * FT_IN_MILE,
            100 * FT_IN_MILE, 200 * FT_IN_MILE, 500 * FT_IN_MILE, 1000 * FT_IN_MILE, 2000 * FT_IN_MILE};

    float mXOffset = 10;  // in px
    float mYOffset = 10;  // in px
    private float mLineWidth = 2;  // in dp
    private float mTextSize = 15; // in dp
    private float mTextPadding = 2; // in dp

    public ColorMode mColorMode = ColorMode.COLOR_MODE_AUTO;

    private GoogleMap mMap;

    float mXdpi;
    float mYdpi;

    public MapScaleBar(Context context) {
        this(context, null);
    }

    public MapScaleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MapScaleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mXdpi = context.getResources().getDisplayMetrics().xdpi;
        mYdpi = context.getResources().getDisplayMetrics().ydpi;

        setLineWidth(mLineWidth);
        setTextSize(mTextSize);
        setTextPadding(mTextPadding);
    }

    public void addTarget(GoogleMap map) {
        this.mMap = map;

        map.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                invalidate();
            }
        });
    }

    public void setLineWidth(float lineWidth) {
        this.mLineWidth = pxToDp(lineWidth);
    }

    public void setTextSize(float textSize) {
        this.mTextSize = pxToDp(textSize);
    }

    public void setTextPadding(float textPadding) {
        this.mTextPadding = pxToDp(textPadding);
    }

    public float pxToDp(float px) {
        return px * (mYdpi / 160);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mMap == null)
            return;

        canvas.save();

        drawScaleBarPicture(canvas);

        canvas.restore();
    }

    private void drawScaleBarPicture(Canvas canvas) {
        // We want the scale bar to be as long as the closest round-number miles/kilometers
        // to 1-inch at the latitude at the current center of the screen.
        int darkC;
        int whiteC;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            darkC = getResources().getColor(R.color.text_primary_dark, null);
            whiteC = getResources().getColor(R.color.text_primary_light, null);
        }else {
            darkC = getResources().getColor(R.color.text_primary_dark);
            whiteC = getResources().getColor(R.color.text_primary_light);
        }

       // int darkC = getResources().getColor(R.color.text_primary_dark, null);
       // int whiteC = ResourcesUtil.getColor(getResources(), R.color.text_primary_light, null);

        if (mColorMode == ColorMode.COLOR_MODE_WHITE ||
                mColorMode == ColorMode.COLOR_MODE_AUTO && (mMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE || mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID))
            drawXMetric(canvas, whiteC, darkC);
        else
            drawXMetric(canvas, darkC, whiteC);
    }

    private void drawXMetric(Canvas canvas, int fillColor, int outlineColor) {
        Projection projection = mMap.getProjection();

        LatLng p1 = projection.fromScreenLocation(new Point(
                (int) ((getWidth() / 2) - (mXdpi / 2)), getHeight() / 2));
        LatLng p2 = projection.fromScreenLocation(new Point((int) (
                (getWidth() / 2) + (mXdpi / 2)), getHeight() / 2));

        Location locationP1 = new Location("ScaleBar location p1");
        Location locationP2 = new Location("ScaleBar location p2");

        locationP1.setLatitude(p1.latitude);
        locationP2.setLatitude(p2.latitude);
        locationP1.setLongitude(p1.longitude);
        locationP2.setLongitude(p2.longitude);


        // PAINTS
        final Paint barPaintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaintFill.setStyle(Paint.Style.FILL);
        barPaintFill.setColor(fillColor);

        final Paint barPaintStroke = new Paint(barPaintFill);
        barPaintStroke.setColor(outlineColor);
        barPaintStroke.setStyle(Paint.Style.STROKE);
        barPaintStroke.setStrokeWidth(mLineWidth/5);

        final Paint textPaintFill = new Paint(barPaintFill);
        textPaintFill.setTextSize(mTextSize);

        final Paint textPaintStroke = new Paint(barPaintStroke);
        textPaintStroke.setTextSize(mTextSize);

        // LENGTH
        float xMetersPerInch = locationP1.distanceTo(locationP2);

        int nearestM = findNextSmallestInUnit(xMetersPerInch, false);
        int nearestFT = findNextSmallestInUnit(xMetersPerInch, true);
        float lengthM = mXdpi / xMetersPerInch * nearestM;
        float lengthFT = mXdpi / xMetersPerInch * (nearestFT / 3.2808f);
        String msgM = scaleBarLengthText(nearestM, false);
        String msgFT = scaleBarLengthText(nearestFT, true);

        float longest = Math.max(lengthFT, lengthM);

        // Get text rects & cords
        Rect textRectM = new Rect();
        textPaintFill.getTextBounds(msgM, 0, msgM.length(), textRectM);
        Rect textRectFT = new Rect();
        textPaintFill.getTextBounds(msgFT, 0, msgFT.length(), textRectFT);

        PointF textCoordM = new PointF(canvas.getWidth() - mXOffset - textRectM.width() - mTextPadding,
                canvas.getHeight() - mYOffset - textRectFT.height() - mTextPadding * 2 - mLineWidth);
        PointF textCoordFT = new PointF(canvas.getWidth() - mXOffset - textRectFT.width() - mTextPadding,
                canvas.getHeight() - mYOffset - textRectFT.height() - textRectFT.top);
        // PointF textCoordFT = new PointF()

        // All movements are based in the bottom, right corner
        Path barP = new Path();
        barP.setFillType(Path.FillType.EVEN_ODD);
        barP.moveTo(canvas.getWidth() - mXOffset, canvas.getHeight() - mYOffset - textRectFT.height() - mTextPadding);

        barP.rLineTo(- lengthFT + mLineWidth, 0);
        barP.rLineTo(0, textRectFT.height() * 0.6f);
        barP.rLineTo(- mLineWidth, 0);
        barP.rLineTo(0, -textRectFT.height() * 0.6f);
        barP.rLineTo(lengthFT - longest, 0);
        barP.rLineTo(0, - mLineWidth);
        barP.rLineTo(longest - lengthM, 0);
        barP.rLineTo(0, -textRectM.height() * 0.6f);
        barP.rLineTo(mLineWidth, 0);
        barP.rLineTo(0, textRectM.height() * 0.6f);
        barP.rLineTo(lengthM - mLineWidth, 0);
        barP.close();

        canvas.drawPath(barP, barPaintFill);
        canvas.drawPath(barP, barPaintStroke);

        canvas.drawText(msgM, textCoordM.x, textCoordM.y, textPaintStroke);
        canvas.drawText(msgM, textCoordM.x, textCoordM.y, textPaintFill);

        canvas.drawText(msgFT, textCoordFT.x, textCoordFT.y, textPaintStroke);
        canvas.drawText(msgFT, textCoordFT.x, textCoordFT.y, textPaintFill);
    }

    private int findNextSmallestInUnit(float meters, boolean imperial) {
        int[] data = imperial ? FT : METERS;
        float value = meters * (imperial ? 3.2808f : 1);

        int closest = data[0];

        for (int elem: data) {
            if (elem - value > 0)
                break;
            else
                closest = elem;
        }

        return closest;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int)mXdpi, (int)(mTextPadding * 4 + mTextSize * 2 + mLineWidth));
    }

    private String scaleBarLengthText(int value, boolean imperial) {
        if (imperial) {
            if (value >= FT_IN_MILE)
                return String.format(Locale.getDefault(), "%d mi", value / FT_IN_MILE);
            else
                return String.format(Locale.getDefault(), "%d ft", value);
        } else {
            if (value >= 1000)
                return String.format(Locale.getDefault(), "%d km", value / 1000);
            else
                return String.format(Locale.getDefault(), "%d m", value);
        }
    }
}