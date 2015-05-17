package com.ap.APListView;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;

/**
 * Created by andrejp on 16.5.2015.
 */
public class APListScroller {

    private final Resources resources;

    private APListView listView;
    private SectionIndexer sectionIndexer;
    private String[] sections;

    private boolean indexing = false;
    private final float indexWidth;
    private final float indexMargin;
    private final float hintSide;
    private RectF indexbar;
    private RectF hint;
    private final Paint indexPaint;
    private final Paint sectionPaint;
    private final Paint hintPaint;
    private final float density;
    private float sectionHeight;
    private int currentIndex;

    public APListScroller(Context context, APListView listView) {
        this.listView = listView;
        resources = context.getResources();

        density = resources.getDisplayMetrics().density;

        indexWidth = 12 * density;
        indexMargin = 10 * density;
        hintSide = 80 * density;

        indexPaint = new Paint();
        indexPaint.setColor(resources.getColor(android.R.color.white));
        indexPaint.setAlpha(154);
        indexPaint.setAntiAlias(true);

        sectionPaint = new Paint();
        sectionPaint.setColor(resources.getColor(android.R.color.black));
        sectionPaint.setAlpha(192);
        sectionPaint.setAntiAlias(true);
        sectionPaint.setTextSize(11 * density);

        hintPaint = new Paint();
        hintPaint.setColor(resources.getColor(android.R.color.black));
        hintPaint.setAlpha(192);
        hintPaint.setAntiAlias(true);
        hintPaint.setTextSize(40 * density);
    }

    public void setAdapter(ListAdapter listAdapter) {
        sectionIndexer = (SectionIndexer) listAdapter;
        sections = (String[]) sectionIndexer.getSections();
    }

    public void draw(Canvas canvas) {
        drawIndex(canvas);
        drawSections(canvas);
        if (indexing) {
            drawHint(canvas);
        }
    }

    private void drawSections(Canvas canvas) {
        float paddingTop = (sectionHeight - (sectionPaint.descent() - sectionPaint.ascent())) / 2;
        for (int i = 0; i < sections.length; i++) {
            float paddingLeft = (indexWidth - sectionPaint.measureText(sections[i])) / 2;
            canvas.drawText(sections[i], indexbar.left + paddingLeft, indexbar.top + sectionHeight * i + paddingTop - sectionPaint.ascent(), sectionPaint);
        }
    }

    private void drawIndex(Canvas canvas) {
        canvas.drawRoundRect(indexbar, 2 * density, 2 * density, indexPaint);
    }

    private void drawHint(Canvas canvas) {
        canvas.drawRoundRect(hint, 10 * density, 10 * density, indexPaint);
        float paddingLeft = (hintSide - hintPaint.measureText(sections[currentIndex])) / 2;
        float paddingTop = (hintSide - (hintPaint.descent() - hintPaint.ascent())) / 2;
        canvas.drawText(sections[currentIndex], hint.left + paddingLeft, hint.top + paddingTop - hintPaint.ascent(), hintPaint);
    }

    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (ev.getX() >= indexbar.left) {
                    indexing = true;
                    currentIndex = getSectionByPoint(ev.getY());
                    listView.setSelection(sectionIndexer.getPositionForSection(currentIndex));
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (indexing) {
                    currentIndex = getSectionByPoint(ev.getY());
                    listView.smoothScrollToPositionFromTop(sectionIndexer.getPositionForSection(currentIndex), 0, 100);
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (indexing) {
                    indexing = false;
                }
                break;
        }
        return false;
    }

    private int getSectionByPoint(float y) {
        if (sections == null || sections.length == 0 || y < indexbar.top) {
            return 0;
        } else if (y >= indexbar.bottom) {
            return sections.length - 1;
        } else {
            float relY = y - indexbar.top;
            return (int) (relY / sectionHeight);
        }
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        indexbar = new RectF(w - indexMargin - indexWidth, indexMargin, w - indexMargin, h - indexMargin);
        hint = new RectF((w - hintSide) / 2, (h - hintSide) / 2, (w + hintSide) / 2, (h + hintSide) / 2);
        if (sections != null) {
            sectionHeight = indexbar.height() / sections.length;
        }
    }
}
