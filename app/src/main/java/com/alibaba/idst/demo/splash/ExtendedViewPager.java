package com.alibaba.idst.demo.splash;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by HashWaney on 2019/8/20.
 */

public class ExtendedViewPager extends ViewPager {

    private boolean mPagingEnable = true;

    public ExtendedViewPager(@NonNull Context context) {
        this(context, null);

    }

    public ExtendedViewPager(@Nullable Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
        mPagingEnable = true;
    }

    @Override
    public boolean onInterceptHoverEvent(MotionEvent event) {
        if (!mPagingEnable)
            return false;
        return super.onInterceptHoverEvent(event);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mPagingEnable) return false;
        return super.onTouchEvent(ev);

    }

    public void setPagingEnable(boolean enable) {
        this.mPagingEnable = enable;
    }
}
