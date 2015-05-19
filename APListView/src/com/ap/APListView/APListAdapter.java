package com.ap.APListView;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
        return itemList.size();
    }

    @Override
    public APItem getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        APItem apItem = getItem(position);
        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(apItem.toString());
        if (apItem.isHeader()) {
            textView.setBackgroundResource(android.R.color.darker_gray);
            textView.setTextColor(context.getResources().getColor(android.R.color.black));
            textView.setTextSize(20);
            textView.setTypeface(null, Typeface.BOLD);
            int section = getSectionForPosition(position);
            convertView.setTag(section);
            headerMap.put(section, convertView);
        } else {
            textView.setBackgroundResource(android.R.color.black);
            textView.setTextColor(context.getResources().getColor(android.R.color.white));
            textView.setTextSize(14);
            textView.setTypeface(null, Typeface.NORMAL);
            convertView.setTag(-1);
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
                return itemList.indexOf(item);
            }
        }
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return getItem(position).toString().charAt(0) - CHAR_A;
    }

    public View getHeaderForSection(int section) {
        View headerForSection = headerMap.get(section);
        if (headerForSection.getTag() == (Integer)section) {
            return headerForSection;
        } else {
            return null;
        }
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
