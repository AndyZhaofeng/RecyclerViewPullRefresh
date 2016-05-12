package com.example.zhaofeng.recyclerviewpullrefresh;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zhaofeng on 16/5/9
 */
public class RecyclerViewRefresh extends LinearLayout
{
    private static final String LOG_TAG=RecyclerViewRefresh.class.getSimpleName();
    static final int STATE_PRESS=0;
    static final int STATE_DRAG_DOWN=1;
    static final int STATE_DRAG_UP=2;
    static final int STATE_RELEASE_DOWN=3;
    static final int STATE_RELEASE_UP=4;
    private static final int INVALID_POINTER=-1;
    //Default offset in dips from the top of the view to where the progress
    //spinner should stop
    private static final int DEFAULT_CIRCLE_TARGET=64;
    private static final float DRAG_RATE=.5f;

    private View headerView,footerView,thisView;
    private View mTarget; //the target of the gesture
    private ImageView arrowIv;
    private TextView refreshTv;
    private OnPullToRefresh refreshListener;
    private OnDragToLoad loadListener;
    float startY=0;

    private int headerHeight=0;
    private boolean mReturningToStart;
    private boolean mRefreshing=false;
    private boolean mNestedScrollInProgress;
    private int mCurrentTargetOffsetTop;
    protected int mOriginalOffsetTop;
    private boolean mIsBeingDragged;
    private int mActivePointerId=INVALID_POINTER;
    private float mInitailDownY;
    private int mTouchSlop;
    private float mTotalDragDistance=-1;
    private float mInitialMotionY;
    private float mSpinnerFinalOffset;
    private boolean updateHeader=true;
    private Handler handler=new Handler();
    private Timer timer;

    public RecyclerViewRefresh(Context context) {
        super(context);
        initView(context);
    }

    public RecyclerViewRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RecyclerViewRefresh(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }
    private void initView(Context context)
    {
        thisView=this;
        mTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();
        headerView=LayoutInflater.from(context).inflate(R.layout.header_layout,null);
        footerView=LayoutInflater.from(context).inflate(R.layout.header_layout,null);
        measureView(headerView);
        arrowIv=(ImageView)headerView.findViewById(R.id.arrow);
        refreshTv=(TextView)headerView.findViewById(R.id.tip);
        headerHeight=headerView.getMeasuredHeight();
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                headerView.getMeasuredHeight());
        this.addView(headerView,lp);
        setTopHeader(headerHeight);

        final DisplayMetrics metrics=getResources().getDisplayMetrics();
        mSpinnerFinalOffset=DEFAULT_CIRCLE_TARGET*metrics.density;
        mTotalDragDistance=mSpinnerFinalOffset;
    }
    /**
     * 通知父布局，占用的宽，高；
     *
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int height;
        int tempHeight = p.height;
        if (tempHeight > 0) {
            height = MeasureSpec.makeMeasureSpec(tempHeight,
                    MeasureSpec.EXACTLY);
        } else {
            height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(width, height);
    }
    private void setTopHeader(int height)
    {
//        ViewGroup.LayoutParams lp=headerView.getLayoutParams();
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
        {
            this.setY(-height);
        }else{
            LayoutParams lp=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,height);
            lp.topMargin=-height;
            this.setLayoutParams(lp);
        }
        headerView.invalidate();
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
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        final int action=MotionEventCompat.getActionMasked(ev);

        if(mReturningToStart && action == MotionEvent.ACTION_DOWN){
            mReturningToStart = false;
        }

        if(!isEnabled() || mReturningToStart || canChildScrollUp()
                ||mRefreshing || mNestedScrollInProgress){
            return false;
        }

        switch (action){
            case MotionEvent.ACTION_DOWN:
                setTargetOffsetTopAndBottom(mOriginalOffsetTop-headerView.getTop(),true);
                mActivePointerId=MotionEventCompat.getPointerId(ev,0);
                mIsBeingDragged=false;
                final float initialDownY=getMotionEventY(ev,mActivePointerId);
                if(initialDownY==-1){
                    return false;
                }
                mInitailDownY=initialDownY;
                break;
            case MotionEvent.ACTION_MOVE:
                if(mActivePointerId==INVALID_POINTER){
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.");
                    return false;
                }
                final float y=getMotionEventY(ev,mActivePointerId);
                if(y==-1){
                    return false;
                }
                final float yDiff=y-mInitailDownY;
                if(yDiff>mTouchSlop && !mIsBeingDragged){
                    mInitialMotionY=mInitailDownY+mTouchSlop;
                    mIsBeingDragged=true;
                }
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsBeingDragged=false;
                mActivePointerId=INVALID_POINTER;
                break;
        }
        return mIsBeingDragged;
    }

    private float getMotionEventY(MotionEvent ev,int activePointerId){
        final int index=MotionEventCompat.findPointerIndex(ev,activePointerId);
        if(index<0){
            return -1;
        }
        return MotionEventCompat.getY(ev,index);
    }
    private void setTargetOffsetTopAndBottom(int offset,boolean requiresUpdate){
        if(this.getTop()<headerHeight)
        {
            this.offsetTopAndBottom(offset);
            mCurrentTargetOffsetTop=this.getTop();
            if(requiresUpdate && Build.VERSION.SDK_INT<11){
                invalidate();
            }

            if(this.getTop()>(headerHeight-30))
            {
                if(updateHeader){
                    updateHeader=false;
                    refreshTv.setText("松开刷新");
                    RotateAnimation animation=new RotateAnimation(0,180,
                            Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                    animation.setDuration(800);
                    animation.setFillAfter(true);
                    arrowIv.startAnimation(animation);
                }
            }
        }

    }

    private void onSecondaryPointerUp(MotionEvent ev){
        final int pointerIndex=MotionEventCompat.getActionIndex(ev);
        final int pointerId=MotionEventCompat.getPointerId(ev,pointerIndex);
        if(pointerId==mActivePointerId){
            //This was our active pointer going up. Choose a new
            //active pointer and adjust accordingly.
            final int newPointerIndex=pointerIndex==0?1:0;
            mActivePointerId=MotionEventCompat.getPointerId(ev,newPointerIndex);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        final int action=MotionEventCompat.getActionMasked(event);
        int pointerIndex=-1;

        if(mReturningToStart&&action==MotionEvent.ACTION_DOWN){
            mReturningToStart=false;
        }
        if(!isEnabled() || mReturningToStart
                || canChildScrollUp() || mNestedScrollInProgress){
            //Fail fast if we're not in a state where a swipe is possible
            return false;
        }
        switch(action){
            case MotionEvent.ACTION_DOWN:
                mActivePointerId=MotionEventCompat.getPointerId(event,0);
                mIsBeingDragged=false;
                updateHeader=true;
                break;
            case MotionEvent.ACTION_MOVE:{
                pointerIndex=MotionEventCompat.findPointerIndex(event,mActivePointerId);
                if(pointerIndex<0){
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.");
                    return false;
                }
                final float y=MotionEventCompat.getY(event,pointerIndex);
                final float overscrollTop=(y-mInitialMotionY)*DRAG_RATE;
                if(mIsBeingDragged){
                    if(overscrollTop>0){
                        moveSpinner(overscrollTop);
                    }else{
                        return false;
                    }
                }
                break;
            }
            case MotionEventCompat.ACTION_POINTER_DOWN:{
                pointerIndex=MotionEventCompat.getActionIndex(event);
                if(pointerIndex<0){
                    Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.");
                    return false;
                }
                mActivePointerId=MotionEventCompat.getPointerId(event,pointerIndex);
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointerUp(event);
                break;
            case MotionEvent.ACTION_UP:{
                pointerIndex=MotionEventCompat.findPointerIndex(event,mActivePointerId);
                if(pointerIndex<0){
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return false;
                }
                final float y=MotionEventCompat.getY(event,pointerIndex);
                final float overscrollTop=(y-mInitialMotionY)*DRAG_RATE;
                mIsBeingDragged=false;
                finishSpinner(overscrollTop);
                mActivePointerId=INVALID_POINTER;
                return false;
            }
            case MotionEvent.ACTION_CANCEL:
                return false;
        }
        return true;
    }

    private void moveSpinner(float overscrollTop){
        float originalDragPercent=overscrollTop/mTotalDragDistance;
        float dragPercent=Math.min(1f,Math.abs(originalDragPercent));
        float adjustedPercent=(float)Math.max(dragPercent-.4,0)*5/3;
        float extraOS=Math.abs(overscrollTop)-mTotalDragDistance;
        float slingshotDist=mSpinnerFinalOffset;
        float tensionSlingshotPercent=Math.max(0,Math.min(extraOS,slingshotDist*2)/slingshotDist);
        float tensionPercent=(float)((tensionSlingshotPercent/4)-Math.pow(
                (tensionSlingshotPercent/4),2))*2f;
        float extraMove=(slingshotDist)*tensionPercent*2;

        int targetY=mOriginalOffsetTop+(int)((slingshotDist*dragPercent)+extraMove);
        setTargetOffsetTopAndBottom(targetY-mCurrentTargetOffsetTop,true);
    }
    private void finishSpinner(float overscrollTop){
        if(overscrollTop>mTotalDragDistance){
//            setRefreshing(true,true);
        }else{
            //cancel refresh
            mRefreshing=false;

        }
        animateOffsetToStartPosition();
    }
    private void animateOffsetToStartPosition(){
//        TranslateAnimation animation=new TranslateAnimation(this.getX(),this.getX(),
//                0,-headerHeight);
//        animation.setDuration(800);
//        animation.setFillAfter(true);
//        this.startAnimation(animation);
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(thisView.getTop()>0)
                        {
                            thisView.offsetTopAndBottom(-3);
                            mCurrentTargetOffsetTop = headerView.getTop();
                            if ( Build.VERSION.SDK_INT < 11) {
                                invalidate();
                            }
                        }else{
                            timer.cancel();
                        }
                    }
                });
            }
        },10,10);

        if(this.getTop()<headerHeight) {


        }
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
