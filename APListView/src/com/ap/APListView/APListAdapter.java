package com.ap.APListView;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrejp on 16.5.2015.
 */
public class APListAdapter<E> extends BaseAdapter implements SectionIndexer {

    private static final char CHAR_A = 'A';
    private static final char CHAR_Z = 'Z';

    private Context context;
    private List<APItem> itemList;
    private Map<Integer, View> headerMap;

    private int hiddenPosition = -1;
    private int placeholderHeight;

    public APListAdapter(Context context, List<E> objectList) {
        this.context = context;
        initializeHeaderedList(objectList);
        headerMap = new HashMap<>();
    }

    private void initializeHeaderedList(List<E> objectList) {
        this.itemList = new ArrayList<>();
        if (objectList.isEmpty()) {
            return;
        }
        char currentHeaderChar = CHAR_A - 1;
        for (E object : objectList) {
            char objectHeaderChar = object.toString().toUpperCase().charAt(0);
            if (objectHeaderChar > currentHeaderChar) {
                APHeaderItem headerItem = new APHeaderItem(objectHeaderChar);
                itemList.add(headerItem);
                currentHeaderChar = objectHeaderChar;
            }
            itemList.add(new APListItem(object));
        }
    }

    @Override
    public int getCount() {
        return itemList.size() * 2;
    }

    @Override
    public APItem getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position / 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position % 2 == 1 || position == hiddenPosition) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_emptyview, parent, false);
            convertView.setTag("placeholder");
            if (position == hiddenPosition) {
                convertView.setLayoutParams(new ViewGroup.LayoutParams(convertView.getLayoutParams().width, placeholderHeight));
            }
            return convertView;
        }
        if (convertView == null || convertView.getTag().equals("placeholder")) {
            convertView = LayoutInflater.from(context).inflate(R.layout.listview_item, parent, false);
        }
        APItem apItem = getItem(position / 2);
        TextView textView = (TextView) convertView.findViewById(R.id.text);
        ImageView drag = (ImageView) convertView.findViewById(R.id.drag_icon);
        textView.setText(apItem.toString());
        if (apItem.isHeader()) {
            convertView.setBackgroundResource(android.R.color.darker_gray);
            textView.setTextColor(context.getResources().getColor(android.R.color.black));
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);
            int section = getSectionForPosition(position);
            convertView.setTag(section);
            drag.setVisibility(View.GONE);
            headerMap.put(section, convertView);
        } else {
            convertView.setBackgroundResource(android.R.color.black);
            textView.setTextColor(context.getResources().getColor(android.R.color.white));
            textView.setTextSize(14);
            textView.setTypeface(null, Typeface.NORMAL);
            convertView.setTag(-1);
            drag.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    @Override
    public Object[] getSections() {
        String sections[] = new String[CHAR_Z - CHAR_A + 1];
        for (int i = 0; i < sections.length; i++) {
            sections[i] = Character.toString((char) (CHAR_A + i));
        }
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (APItem item : itemList) {
            if (item.isHeader() && item.toString().toUpperCase().charAt(0) - CHAR_A == sectionIndex) {
                return itemList.indexOf(item) * 2;
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return getItem(position / 2).toString().charAt(0) - CHAR_A;
    }

    public View getHeaderForSection(int section) {
        View headerForSection = headerMap.get(section);
        if (headerForSection.getTag() == (Integer)section) {
            return headerForSection;
        } else {
            return null;
        }
    }

    public void showPosition(int position) {
        hiddenPosition = -1;
        notifyDataSetChanged();
    }

    public void hidePosition(int position) {
        hiddenPosition = position;
        notifyDataSetChanged();
    }

    public void setPlaceholderHeight(int placeholderHeight) {
        this.placeholderHeight = placeholderHeight;
    }

    private abstract class APItem {
        public abstract String toString();
        public abstract boolean isHeader();
    }

    private class APListItem extends APItem {

        private E itemObject;

        public APListItem(E itemObject) {
            this.itemObject = itemObject;
        }

        @Override
        public String toString() {
            return itemObject.toString();
        }

        @Override
        public boolean isHeader() {
            return false;
        }
    }

    private class APHeaderItem extends APItem {
        private char headerChar;

        public APHeaderItem(char headerChar) {
            this.headerChar = headerChar;
        }

        public String toString() {
            return String.format("%c", headerChar);
        }

        @Override
        public boolean isHeader() {
            return true;
        }
    }
}
