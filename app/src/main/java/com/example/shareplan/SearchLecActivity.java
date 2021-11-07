package com.example.shareplan;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchLecActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lec);

        ListView listView = (ListView) findViewById(R.id.search_listView);

        ListViewAdapter adapter = new ListViewAdapter(SearchLecActivity.this);
        listView.setAdapter(adapter);

        adapter.addItem("모바일 프로그래밍", "이창우/2분반/월수 10:30-12:00");

    }
}
