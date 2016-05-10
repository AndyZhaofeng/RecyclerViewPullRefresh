package com.example.zhaofeng.recyclerviewpullrefresh;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zhaofeng on 16/5/10.
 */
public class LinearLayoutRefreshManager extends SwipeRefreshLayout
{
    public LinearLayoutRefreshManager(Context context) {
        super(context);
    }

    public LinearLayoutRefreshManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
