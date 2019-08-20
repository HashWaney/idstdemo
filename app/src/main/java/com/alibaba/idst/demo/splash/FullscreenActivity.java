package com.alibaba.idst.demo.splash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.alibaba.idst.demo.R;

/**
 * Created by HashWaney on 2019/8/20.
 */

public class FullscreenActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    private ExtendedViewPager mVpVideo;
    private TextView mTvEnter;
    private CirclePageIndicator mViewPagerIndicator;
    private boolean mVisible;
    private ViewPagerAdapter mvAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_fullscreen);
        mVisible = true;
        mVpVideo = (ExtendedViewPager) findViewById(R.id.vp_video);
        mvAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mVpVideo.setAdapter(mvAdapter);
        mVpVideo.setOffscreenPageLimit(4);

        mTvEnter = ((TextView) findViewById(R.id.tv_enter));
        mTvEnter.setOnClickListener(this);

        mViewPagerIndicator = findViewById(R.id.view_pager_indicator);
        mViewPagerIndicator.setViewPager(mVpVideo);
        mViewPagerIndicator.setOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        ((VideoItemFragment) mvAdapter.getItem(position)).play();

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final int[] videoRes;
        private final int[] slogoImageRes;


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
            this.videoRes = new int[]{R.raw.splash_1, R.raw.splash_2, R.raw.splash_3, R.raw.splash_4};
            this.slogoImageRes = new int[]{R.drawable.slogan_1, R.drawable.slogan_2, R.drawable.slogan_3, R.drawable.slogan_4};
        }

        @Override
        public Fragment getItem(int position) {
            VideoItemFragment videoItemFragment = new VideoItemFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putInt("videoRes", this.videoRes[position]);
            bundle.putInt("imgRes", this.slogoImageRes[position]);
            videoItemFragment.setArguments(bundle);
            if (position < getCount()) {
                return videoItemFragment;
            }
            throw new RuntimeException("Position out of range. Adapter has" + getCount() + "items");

        }

        @Override
        public int getCount() {
            return this.videoRes.length;
        }
    }

    public void next(int pos) {
        int i = this.mVpVideo.getCurrentItem();
        if (pos == i) {
            pos += 1;
        }
        this.mVpVideo.setCurrentItem(pos, true);
    }


}
