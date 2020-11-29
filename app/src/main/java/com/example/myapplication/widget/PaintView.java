package com.example.myapplication.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.myapplication.R;

import java.util.ArrayList;

public class PaintView extends View {
    Bitmap btmBackground, btmView, image, captureImage, originalImage, rotateImage;
    Paint mPaint = new Paint();
    Path mPath = new Path();
    int colorBackground, sizeBrush, sizeEraser;
    float mX, mY;
    Canvas mCanvas;
    int DIFFERENCE_SPACE = 4;
    ArrayList<Bitmap> listAction = new ArrayList<>();
    int leftImage = 50, topImage = 50;
    float refX, refY;
    public boolean toMove = false;
    boolean toResize = false;
    int xCenter, yCenter;
    float xRotate, yRotate;
    private int angle = 0;


    public PaintView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        sizeBrush = sizeEraser = 12;
        colorBackground = Color.WHITE;
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(toPx(sizeBrush));
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        rotateImage = BitmapFactory.decodeResource(getResources(), R.drawable.rotate, options);
        rotateImage = Bitmap.createScaledBitmap(rotateImage, 100, 100, true);
        captureImage = BitmapFactory.decodeResource(getResources(), R.drawable.capture, options);
        captureImage = Bitmap.createScaledBitmap(captureImage, 200, 200, true);
    }

    private float toPx(int sizeBrush) {
        return sizeBrush * (getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        btmBackground = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        btmView = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(btmView);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(colorBackground);
        canvas.drawBitmap(btmBackground, 0, 0, null);
        if (image != null && toMove) {
            drawImage(canvas);
//            canvas.drawBitmap(image, leftImage, topImage, null);
            xCenter = leftImage + image.getWidth() / 2 - captureImage.getWidth() / 2;
            yCenter = topImage + image.getHeight() / 2 - captureImage.getHeight() / 2;
            xRotate = leftImage + image.getWidth() + toPx(10);
            yRotate = topImage - toPx(10);
            canvas.drawBitmap(rotateImage, xRotate, yRotate, null);
            canvas.drawBitmap(captureImage, xCenter, yCenter, null);
        }
        canvas.drawBitmap(btmView, 0, 0, null);
    }

    private void drawImage(Canvas canvas) {
        Matrix matrix = new Matrix();
        matrix.setRotate(angle, image.getWidth() / 2, image.getHeight() / 2);
        matrix.postTranslate(leftImage, topImage);
        canvas.drawBitmap(image, matrix, null);
    }

    public void setColorBackground(int color) {
        colorBackground = color;
        invalidate();
    }

    public void setSizeBrush(int s) {
        sizeBrush = s;
        mPaint.setStrokeWidth(sizeBrush);
    }

    public int getSizeBrush() {
        return this.sizeBrush;
    }

    public void setBrushColor(int color) {
        mPaint.setColor(color);
    }

    public void setSizeEraser(int s) {
        sizeEraser = s;
        mPaint.setStrokeWidth(toPx(sizeEraser));
    }

    public int getSizeEraser() {
        return this.sizeEraser;
    }

    public void enableEraser() {
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void disableEraser() {
        mPaint.setXfermode(null);
        mPaint.setShader(null);
        mPaint.setMaskFilter(null);
    }

    public void addLastAction(Bitmap bitmap) {
        listAction.add(bitmap);
    }

    public void returnLastAction() {
        if (listAction.size() > 0) {
            listAction.remove(listAction.size() - 1);
            if (listAction.size() > 0) {
                btmView = listAction.get(listAction.size() - 1);
            } else {
                btmView = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            }
            mCanvas = new Canvas(btmView);
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                refX = x;
                refY = y;
                if (toMove) {

                    if (isToResize(refX, refY)) {
                        toResize = true;
                    } else {
                        toResize = false;
                    }

                    if ((refX >= xCenter && refX < xCenter + captureImage.getWidth()) && (refY >= yCenter && refY < yCenter + captureImage.getHeight())) {
                        Canvas newCanvas = new Canvas(btmBackground);
//                        newCanvas.drawBitmap(image, leftImage, topImage, null);
                        drawImage(newCanvas);
                        invalidate();

                    }
                    if ((refX >= xRotate && refX <= xRotate + rotateImage.getWidth())
                            && (refY >= yRotate && refY <= yRotate + rotateImage.getHeight())) {
                        angle += 45;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (!toMove)
                    touchMove(x, y);
                else {
                    float nX = event.getX();
                    float nY = event.getY();
                    if (toResize) {
                        int xScale = 0;
                        int yScale = 0;
                        if (nX > refX) {
                            xScale = (int) (image.getWidth() + (nX - refX));
                        } else {
                            xScale = (int) (image.getWidth() - (refX - nX));
                        }
                        if (nY > refY) {
                            yScale = (int) (image.getHeight() + (nY - refY));
                        } else {
                            yScale = (int) (image.getHeight() - (refY - nY));
                        }
                        if (xScale > 0 && yScale > 0)
                            image = Bitmap.createScaledBitmap(originalImage, xScale, yScale, false);
                    }
                    leftImage += nX - refX;
                    topImage += nY - refY;
                    refX = nX;
                    refY = nY;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!toMove) {
                    touchUp();
                    addLastAction(getBitmap());

                }
                break;

        }

        return true;
    }

    private boolean isToResize(float refX, float refY) {
        if (refX >= leftImage && refX < leftImage + image.getWidth()
                && (refY >= topImage && refY <= topImage + 20) || (refY >= topImage + image.getHeight() - 20 && refY <= topImage + image.getHeight())) {
            return true;
        }
        return false;
    }

    private void touchUp() {
        mPath.reset();
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= DIFFERENCE_SPACE || dy >= DIFFERENCE_SPACE) {
            mPath.quadTo(x, y, (x + mX) / 2, (y + mY) / 2);
            mY = y;
            mX = x;
            mCanvas.drawPath(mPath, mPaint);
            invalidate();
        }
    }

    private void touchStart(float x, float y) {
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    public Bitmap getBitmap() {
        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void setImage(Bitmap bitmap) {
        toMove = true;
        image = Bitmap.createScaledBitmap(bitmap, getWidth() / 2, getHeight() / 2, true);
        originalImage = image;
        invalidate();
    }
}
