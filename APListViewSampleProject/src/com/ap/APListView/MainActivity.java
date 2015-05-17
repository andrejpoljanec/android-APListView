package com.ap.APListView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        List<String> dummyData = new ArrayList<>();
        for (int i = 'A'; i <= 'Z'; i++) {
            for (int j = 0; j < 10; j++) {
                dummyData.add(String.format("%c%d", i, j));
            }
        }

        ListView listView = (ListView) findViewById(R.id.aplistview);
        listView.setAdapter(new APListAdapter<>(this, dummyData));
    }
}
