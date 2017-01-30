package com.in.den.android.aquarium;


import android.content.Context;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by harumi on 12/01/2017.
 */


public class Animal extends ImageView {

    private Context mContext;
    private float mVx, mVy;
    private int mSize = 200;


    public Animal(Context context, float x, float y, float vx, float vy) {
        super(context);

        mContext = context;
        mVx = vx;
        mVy = vy;
        setX(x);
        setY(y);
        setLayoutParams(new FrameLayout.LayoutParams(mSize, mSize));

    }

    public Animal(Context context, float x, float y, float vx, float vy, int idDrawable) {
        this(context, x, y, vx, vy);
        setImageResource(idDrawable);
    }


    public void updatePosition() {

        ViewGroup viewparent = (ViewGroup) getParent();
        int parentWidth = viewparent.getWidth();
        int parentHeight = viewparent.getHeight();

        float x = getX();
        float y = getY();

        x = x + mVx;
        y = y + mVy;

        if (x + mSize >= parentWidth || x <= 0) mVx = -mVx;
        if (y + mSize >= parentHeight || y <= 0) mVy = -mVy;

        setX(x);
        setY(y);
    }

    public void changeSpeed (float vx, float vy) {
        mVx = vx;
        mVy = vy;
    }

    public float getXSpeed() {
        return mVx;
    }

    public float getYSpeed() {
        return mVy;
    }

    public static class Coccinelle extends Animal {

        public Coccinelle(Context context, float x, float y, float vx, float vy) {
            super(context, x, y, vx, vy);
            setImageResource(R.drawable.tentoumushi);
        }
    }

    public static class Butterfly extends Animal {

        public Butterfly(Context context, float x, float y, float vx, float vy) {
            super(context, x, y, vx, vy);
            setImageResource(R.drawable.butterfly);
        }
    }

}
