package com.example.zhaofeng.recyclerviewpullrefresh;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhaofeng on 16/5/9.
 */
public class RecyclerViewRefresh extends RecyclerView
{
    View headerView;//顶部布局文件
    int headerHeight;//顶部布局文件的高度；
    int firstVisibleItem;//当前第一个可见的item的位置
    int scrollState;//listview当前滚动状态
    boolean isRemark;//标记，当前是在listview最顶端摁下的
    int startY;//摁下时的Y值

    int state;//当前的状态；
    final int NONE=0;//正常状态
    final int PULL=1;//提示下拉状态
    final int RELESE=2;//提示释放状态
    final int REFLASHING=3;//刷新状态

    public RecyclerViewRefresh(Context context) {
        super(context);
    }

    public RecyclerViewRefresh(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewRefresh(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void initView(Context context)
    {
        LayoutInflater inflater=LayoutInflater.from(context);
        headerView=inflater.inflate(R.layout.header_layout,null);
        measureView(headerView);
        headerHeight=headerView.getMeasuredHeight();
        topPadding(--headerHeight);
    }

    /**
     * 通知父布局，占用的宽、高
     * @param view
     */
    private void measureView(View view)
    {
        ViewGroup.LayoutParams p=view.getLayoutParams();
        if(p==null){
            p=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width=ViewGroup.getChildMeasureSpec(0,0,p.width);
        int height;
        int tempHeight=p.height;
        if(tempHeight>0){
            height=MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.EXACTLY);
        }else{
            height=MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
        }
        view.measure(width,height);
    }

    /**
     * 设置header布局 上边距
     * @param topPadding
     */
    private void topPadding(int topPadding)
    {
        headerView.setPadding(headerView.getPaddingLeft(),topPadding,
                headerView.getPaddingRight(),headerView.getPaddingBottom());
        headerView.invalidate();
    }

}
