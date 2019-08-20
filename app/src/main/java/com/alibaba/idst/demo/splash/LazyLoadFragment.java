package com.alibaba.idst.demo.splash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by HashWaney on 2019/8/20.
 */

public abstract class LazyLoadFragment extends Fragment {

    /**
     * 视图是否初始化
     */
    protected boolean isInit = false;
    protected boolean isLoad = false;
    protected final String TAG = LazyLoadFragment.class.getSimpleName();

    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(setContentView(), container, false);
        isInit = true;
        //初始化加载数据
        isCanLoadData();
        return view;

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
    }

    protected void showToast(String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 设置Fragment要显示的布局
     */
    protected abstract int setContentView();

    /**
     * 获取设置的布局
     */
    protected View getContentView() {
        return view;
    }

    /**
     * 找出对应的控件
     */
    protected <T extends View> T findViewById(int id) {
        return (T) getContentView().findViewById(id);
    }

    /**
     * 是否可以加载数据
     * 1. 视图已经初始化
     * 2. 视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }
        if (getUserVisibleHint()) {
            lazyLoad();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }

    /**
     * 当视图已经对用户不可见并且加载过数据,如果需要在切换到其他页面时停止加载数据,
     */
    protected void stopLoad() {

    }

    /**
     * 当视图已经初始化并且对用户可见的时候真正去加载数据
     */
    protected abstract void lazyLoad();


}
