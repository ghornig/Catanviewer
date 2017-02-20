package com.example.graham.catanviewer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.MotionEvent;
import android.view.View;

public class BoardTile extends View
{
    private Bitmap mCardImage;
    private final Paint mPaint = new Paint();
    private final Point mSize = new Point();
    private final Point mStartPosition = new Point();
    private Region mRegion;

    public BoardTile(Context context)
    {
        super(context);
        mRegion = new Region();
        // this.setOnTouchListener(mTouchListener);
    }

    public final Bitmap getImage() { return mCardImage; }
    public final void setImage(Bitmap image)
    {
        mCardImage = image;
        setSize(mCardImage.getWidth(), mCardImage.getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        Point position = getPosition();
        canvas.drawBitmap(mCardImage, position.x, position.y, mPaint);
    }

    public final void setPosition(final Point position)
    {
        mRegion.set(position.x, position.y, position.x + mSize.x, position.y + mSize.y);
    }

    public final Point getPosition()
    {
        Rect bounds = mRegion.getBounds();
        return new Point(bounds.left, bounds.top);
    }

    public final void setSize(int width, int height)
    {
        mSize.x = width;
        mSize.y = height;

        Rect bounds = mRegion.getBounds();
        mRegion.set(bounds.left, bounds.top, bounds.left + width, bounds.top + height);
    }

    public final Point getSize() { return mSize; }

    // All of the touch interactions. Also uncomment line ~25
/*    public OnTouchListener mTouchListener = new  OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // Is the event inside of this view?
            if(!mRegion.contains((int)event.getX(), (int)event.getY()))
            {
                return false;
            }

            if(event.getAction() == MotionEvent.ACTION_DOWN)
            {
                mStartPosition.x = (int)event.getX();
                mStartPosition.y = (int)event.getY();
                bringToFront();
                return true;
            }
            else if(event.getAction() == MotionEvent.ACTION_MOVE)
            {
                int x = 0, y = 0;

                x = (int)event.getX() - mStartPosition.x;
                y = (int)event.getY() - mStartPosition.y;

                mRegion.translate(x, y);
                mStartPosition.x = (int)event.getX();
                mStartPosition.y = (int)event.getY();

                invalidate();

                return true;
            }
            else
            {
                return false;
            }
        }
    };*/
}