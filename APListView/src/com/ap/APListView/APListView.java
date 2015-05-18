package com.ap.APListView;

import android.content.Context;
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
        listScroller.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return listScroller.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
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
            headerView.setHeaderSize(windowSize.x, headerForSection.getMeasuredHeight());
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