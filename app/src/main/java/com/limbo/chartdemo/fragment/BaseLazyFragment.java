package com.limbo.chartdemo.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by wangqi on 2018/10/11.
 */

public abstract class BaseLazyFragment extends Fragment {
    //模拟
    // 昨收
    public static double mYesterdayClose = 1217.45;
    // 手术
    public static double mContractSize = 1.0;
    // 保留小数位
    public static int mDec = 2;

    protected abstract int getLayoutId();

    protected abstract void loadData();

    private Activity mActivity;
    private boolean isVisibleToUser;
    private boolean isViewCreat;
    private boolean isInitFinish;

    protected View rootView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreat = true;
        rootView = view;
        onLazy();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        this.isVisibleToUser = isVisibleToUser;
        onLazy();
    }

    private void onLazy() {
        if (isViewCreat && isVisibleToUser && !isInitFinish) {
            isInitFinish = true;
            loadData();
        }
    }
}
