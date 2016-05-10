package com.example.zhaofeng.recyclerviewpullrefresh;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

/**
 * Created by zhaofeng on 16/5/9
 */
public class RecyclerViewRefresh extends ViewGroup implements NestedScrollingParent,NestedScrollingChild
{
    static final int STATE_PRESS=0;
    static final int STATE_DRAG_DOWN=1;
    static final int STATE_DRAG_UP=2;
    static final int STATE_RELEASE_DOWN=3;
    static final int STATE_RELEASE_UP=4;

    private View mTarget; //the target of the gesture
    private OnPullToRefresh refreshListener;
    private OnDragToLoad loadListener;

    public RecyclerViewRefresh(Context context) {
        super(context);
    }

    public RecyclerViewRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public RecyclerViewRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Set the listener to be notified when a refresh is triggered via the
     * pull gesture.
     * @param listener
     */
    public void setOnPullToRefresh(OnPullToRefresh listener)
    {
        this.refreshListener=listener;
    }

    /**
     * Set the listener to be notified when a load is triggered via the
     * drag gesture
     * @param listener
     */
    public void setOnDragToLoad(OnDragToLoad listener)
    {
        this.loadListener=listener;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
    private void ensureTarget(){
        if(mTarget==null){
            for(int i=0;i<getChildCount();i++)
            {
                View child=getChildAt(i);
                if(child instanceof RecyclerView)
                {
                    mTarget=child;
                    break;
                }
            }
        }
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up.Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp(){
        if(mTarget==null)
        {
            ensureTarget();
        }
        if(Build.VERSION.SDK_INT<14)
        {
            if(mTarget instanceof AbsListView)
            {
                final AbsListView absListView=(AbsListView)mTarget;
                return absListView.getChildCount()>0
                        &&(absListView.getFirstVisiblePosition()>0
                        ||absListView.getChildAt(0).getTop()<absListView.getPaddingTop());
            }else{
                return ViewCompat.canScrollVertically(mTarget,-1)|| mTarget.getScrollY()>0;
            }
        }else{
            return ViewCompat.canScrollVertically(mTarget,-1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * Classes that wish to be notified when the pull gesture correctly
     * triggers a refresh should implement this interface.
     */
    public interface OnPullToRefresh{
        public void onRefresh();
    }

    /**
     * Classes that wish to be notified when the drag gesture correctly
     * triggers a load should implement this interface.
     */
    public interface OnDragToLoad{
        public void onLoad();
    }
}
