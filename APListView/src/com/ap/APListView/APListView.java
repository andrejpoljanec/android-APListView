package com.ap.APListView;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by andrejp on 16.5.2015.
 */
public class APListView extends ListView {

    private APListScroller listScroller = null;
    private APListHeaderView headerView = null;
    private APListAdapter listAdapter = null;
    private Point windowSize;

    private float dragX;
    private float dragY;
    private float dragXOffset;
    private float dragYOffset;
    private float originalX;
    private float originalY;
    private float dropX;
    private float dropY;
    private Bitmap dragBitmap;
    private ValueAnimator dragAnimator;

    public APListView(Context context) {
        super(context);
        initialize();
    }

    public APListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public APListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public APListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize();
    }

    private void initialize() {
        listScroller = new APListScroller(getContext(), this);
        headerView = new APListHeaderView();

        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowSize = new Point();
        windowManager.getDefaultDisplay().getSize(windowSize);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        headerView.draw(canvas);
        if (dragBitmap != null) {
            canvas.drawBitmap(dragBitmap, dragX, dragY, null);
        }
        listScroller.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (dragBitmap != null && ev.getAction() == MotionEvent.ACTION_MOVE) {
            dragX = ev.getX() - dragXOffset;
            dragY = ev.getY() - dragYOffset;
            invalidate();
            return true;
        } else if (dragBitmap != null && ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            dropX = ev.getX() - dragXOffset;
            dropY = ev.getY() - dragYOffset;
            dragAnimator = ValueAnimator.ofFloat(0, 1);
            dragAnimator.setDuration(200);
            dragAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    dragX = dropX + value * (originalX - dropX);
                    dragY = dropY + value * (originalY - dropY);
                    invalidate();
                }
            });
            dragAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    dragBitmap = null;
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                    dragBitmap = null;
                }
                @Override public void onAnimationStart(Animator animation) {}
                @Override public void onAnimationRepeat(Animator animation) {}
            });
            dragAnimator.start();
            return true;
        }
        return listScroller.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) ev.getX();
            int y = (int) ev.getY();
            if (x >= 64) {
                return false;
            }
            View view = getChildAt(pointToPosition(x, y) - getFirstVisiblePosition());
            view.setDrawingCacheEnabled(true);
            dragBitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            originalX = dragX = view.getLeft();
            originalY = dragY = view.getTop();
            dragXOffset = ev.getX() - dragX;
            dragYOffset = ev.getY() - dragY;
        }
        return true;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter instanceof APListAdapter) {
            listScroller.setAdapter(adapter);
            listAdapter = (APListAdapter) adapter;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        listScroller.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        int section = listAdapter.getSectionForPosition(getFirstVisiblePosition());
        if (listAdapter.getPositionForSection(section) == getFirstVisiblePosition()) {
            // replace header
            View headerForSection = listAdapter.getHeaderForSection(section);
            if (headerForSection == null) {
                return;
            }
            headerForSection.measure(0, 0);
            headerView.setHeader(headerForSection, section);
        } else if (headerView.getPosition() > section){
            // clear header
            headerView.clearHeader();
            headerView.setPosition(section);
        } else if (headerView.getPosition() == section) {
            // scroll header
            View headerForNextSection = listAdapter.getHeaderForSection(section + 1);
            if (headerForNextSection == null) {
                return;
            }
            if (headerForNextSection.getTop() < headerView.getHeight() + 2) {
                headerView.setOffset(headerForNextSection.getTop() - headerView.getHeight() - 2);
            }
        }
    }

}