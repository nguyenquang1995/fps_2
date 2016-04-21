package com.framgia.project1.fps_2_project.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ActiveImageView extends View {
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    public Bitmap mImgBitmap = null;
    private Paint mPaint = new Paint();
    public Matrix mMatrix = new Matrix();
    private Matrix mSavedMatrix = new Matrix();
    private PointF mStart = new PointF();
    private PointF mMid = new PointF();
    private int mMode = NONE;
    private float mOldDist = 1f;
    private float mSumMoveX = 0;
    private float mSumMoveY = 0;

    public ActiveImageView(Context context) {
        super(context);
    }

    public ActiveImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mImgBitmap != null && canvas != null) {
            canvas.drawBitmap(mImgBitmap, mMatrix, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mSavedMatrix.set(mMatrix);
                mStart.set(event.getX(), event.getY());
                mMode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mOldDist = spacing(event);
                mSavedMatrix.set(mMatrix);
                midPoint(mMid, event);
                mMode = ZOOM;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mMode = NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mMode == DRAG) {
                    mMatrix.set(mSavedMatrix);
                    float diffX = event.getX() - mStart.x;
                    float diffY = event.getY() - mStart.y;
                    mSumMoveX += diffX;
                    mSumMoveY += diffY;
                    mMatrix.postTranslate(diffX, diffY);
                    break;
                }
                if (mMode == ZOOM) {
                    float newDist = spacing(event);
                    mMatrix.set(mSavedMatrix);
                    float scale = newDist / mOldDist;
                    mMatrix.postScale(scale, scale, mMid.x, mMid.y);
                    break;
                }
        }
        invalidate();
        return true;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    public void setImageBitmap(Bitmap b) {
        mImgBitmap = b;
    }

    public void returnOldPosition() {
        mMatrix = new Matrix();
        invalidate();
    }
}