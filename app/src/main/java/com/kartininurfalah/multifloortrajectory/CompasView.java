package com.kartininurfalah.multifloortrajectory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;

import java.util.jar.Attributes;

/**
 * Compas view.
 * Created by kartininurfalah on 01/02/18.
 */

public class CompasView extends View{

    private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int width = 0;
    private int height = 0;
    private Matrix matrix; // to manage rotation of the compass view
    private Bitmap bitmap;
    private float bearing; // rotation angle to North

    public CompasView (Context context){
        super(context);
        initialize();
    }

    public CompasView(Context context, Attributes attrib){
        super(context);
        initialize();
    }

    private void initialize() {
        matrix = new Matrix();
        //create bitmap for compass icon
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.compas);
    }

    public void setBearing(float b){
        bearing = b;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        if (bitmapWidth > canvasWidth || bitmapHeight > canvasHeight){
                //resize bitmap to fit in canvas
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    (int) (bitmapWidth * 0.85) , (int) (bitmapHeight * 0.85),
                    true);
        }
        //center
        int bitmapX = bitmap.getWidth() / 2;
        int bitmapY = bitmap.getHeight() / 2;
        int parentX = width / 2;
        int parentY = height / 2;
        int centerX = parentX - bitmapX;
        int centerY = parentY - bitmapY;

        //calculate rotation angle
        int rotation = (int) (360 - bearing);

        //reset matrix
        matrix.reset();
        matrix.setRotate(rotation, bitmapX, bitmapY);
        //center bitmap on canvas
        matrix.postTranslate(centerX, centerY);
        //draw bitmap
        canvas.drawBitmap(bitmap, matrix, paint);
    }
}
