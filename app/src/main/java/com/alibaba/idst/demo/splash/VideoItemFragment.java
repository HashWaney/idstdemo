package com.alibaba.idst.demo.splash;

import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.idst.demo.R;

/**
 * Created by HashWaney on 2019/8/20.
 */

public class VideoItemFragment extends LazyLoadFragment
        implements MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private boolean mHasPaused;
    private int mVideoPosition;
    private int position;
    private int videoRes;
    private int imgRes;
    private FullScreenVideoView mVideoView;
    private ImageView mvSlogan;

    private static Handler handler = new Handler();

    public VideoItemFragment() {
        mHasPaused = false;
        mVideoPosition = 0;
    }


    @Override
    protected int setContentView() {
        return R.layout.video_viewpager_item;
    }

    @Override
    protected void lazyLoad() {
        if (getArguments() == null)
            return;
        position = getArguments().getInt("position");
        videoRes = getArguments().getInt("videoRes");
        imgRes = getArguments().getInt("imgRes");
        Log.e(TAG, "pos:" + position + " videoRes:" + videoRes + " imgRes:" + imgRes);
        mVideoView = findViewById(R.id.vvSplash);
        mvSlogan = findViewById(R.id.ivSlogan);
        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnErrorListener(this);
        mVideoView.setVideoPath("android.resource://" + getActivity().getPackageName() + "/" + videoRes);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mvSlogan.setVisibility(View.VISIBLE);
                        mvSlogan.setImageResource(imgRes);
                    }
                });
            }
        }, 500);

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        FragmentActivity localFragmentAct = getActivity();
        if (localFragmentAct != null && localFragmentAct instanceof FullscreenActivity) {
            ((FullscreenActivity) localFragmentAct).next(position);
        }
        return true;
    }


    @Override
    public void onPrepared(MediaPlayer mp) {
        if (mVideoView != null) {
            mVideoView.requestFocus();
            mVideoView.setOnCompletionListener(this);
            mVideoView.seekTo(0);
            mVideoView.start();
            mp.setLooping(true);
        }
        return;

    }

    @Override
    protected void stopLoad() {
        super.stopLoad();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        FragmentActivity localFragmentAct = getActivity();
        if (localFragmentAct != null && localFragmentAct instanceof FullscreenActivity) {
            ((FullscreenActivity) localFragmentAct).next(position);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mHasPaused) {
            if (mVideoView != null) {
                mVideoView.seekTo(mVideoPosition);
                mVideoView.resume();
            }
        }
        return;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mVideoView != null) {
            mVideoPosition = mVideoView.getCurrentPosition();
        }
        mHasPaused = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
        return;
    }

    public void pause() {
        if (mVideoView != null && mVideoView.canPause()) {
            mVideoView.setOnCompletionListener(this);
            mVideoView.pause();
        }
    }

    public void play() {
        if (mVideoView != null) {
            mVideoView.requestFocus();
            mVideoView.setOnCompletionListener(this);
            mVideoView.seekTo(0);
        } else {
            return;
        }
        mVideoView.start();
    }

    public void reLoadView() {
        if (mVideoView != null) {
            mVideoView.setVideoPath("android.resource://" + getActivity().getPackageName() + "/" + videoRes);
        }
    }

}
