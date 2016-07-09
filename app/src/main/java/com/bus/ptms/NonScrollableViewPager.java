package com.bus.ptms;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by xeonyan on 9/7/2016.
 */
public class NonScrollableViewPager extends ViewPager{
    /**
     *  its the customised viewpager that are not able to swipe manually
     *  alternatively, it 'swipe' programmatically
     */
    private boolean isPagingEnabled = false;

    public NonScrollableViewPager(Context context) {
        super(context);
    }

    public NonScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return this.isPagingEnabled && super.onInterceptTouchEvent(event);
    }

    public void setPagingEnabled(boolean state) {
        this.isPagingEnabled = state;
    }
}
