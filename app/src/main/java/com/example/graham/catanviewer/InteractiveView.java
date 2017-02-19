package com.example.graham.catanviewer;

import android.content.Context;
import android.graphics.Canvas;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class InteractiveView extends RelativeLayout{

    private float mPositionX = 0;
    private float mPositionY = 0;
    private float mScale = 1.0f;

    public InteractiveView(Context context) {
        super(context);
        // Not sure?
        this.setWillNotDraw(false);
        // Configure the touch listener
        this.setOnTouchListener(mTouchListener);
    }

    public void setPosition(float lPositionX, float lPositionY){
        mPositionX = lPositionX;
        mPositionY = lPositionY;
    }

    public void setMovingPosition(float lPositionX, float lPositionY){
        mPositionX += lPositionX;
        mPositionY += lPositionY;
    }

    public void setScale(float lScale){
        mScale = lScale;
    }

    // Used to draw the children(?)
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.translate(mPositionX*mScale, mPositionY*mScale);
        canvas.scale(mScale, mScale);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    // touch events
    private final int NONE = 0;
    private final int DRAG = 1;
    private final int ZOOM = 2;
    private final int CLICK = 3;

    // pinch to zoom
    private float mOldDist;
    private float mNewDist;
    private float mScaleFactor = 0.001f;

    // position
    private float mPreviousX;
    private float mPreviousY;

    int mode = NONE;

    @SuppressWarnings("deprecation")
    public OnTouchListener mTouchListener = new  OnTouchListener(){
        public boolean onTouch(View v, MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN: // one touch: drag
                    mode = CLICK;
                    break;
                case MotionEvent.ACTION_POINTER_2_DOWN: // two touches: zoom
                    mOldDist = spacing(e);
                    mode = ZOOM; // zoom
                    break;
                case MotionEvent.ACTION_UP: // no mode
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_POINTER_2_UP: // no mode
                    mode = NONE;
                    break;
                case MotionEvent.ACTION_MOVE: // rotation
                    if (e.getPointerCount() > 1 && mode == ZOOM) {
                        mNewDist = spacing(e) - mOldDist;

                        mScale += mNewDist*mScaleFactor;
                        invalidate();

                        mOldDist = spacing(e);

                    } else if (mode == CLICK || mode == DRAG) {
                        float dx = (x - mPreviousX)/mScale;
                        float dy = (y - mPreviousY)/mScale;

                        setMovingPosition(dx, dy);
                        invalidate();
                        mode = DRAG;
                    }
                    break;
            }
            mPreviousX = x;
            mPreviousY = y;
            return true;
        }
    };

    // finds spacing
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }
}