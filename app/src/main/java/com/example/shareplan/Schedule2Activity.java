package com.example.shareplan;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Schedule2Activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private reAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TodoInfo> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private CalendarView calendarView;
    private String lec_Uid;

    @Override
    protected void onResume() {
        super.onResume();
        Calendar selectedDate = calendarView.getSelectedDate();
        String datekey = selectedDate.getTime().getYear()+1900 + "-" + (selectedDate.getTime().getMonth()+1) + "-" + selectedDate.getTime().getDate();
        databaseReference.child("TodoInfo").child(lec_Uid).child(datekey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot Tododata : snapshot.getChildren()){
                    TodoInfo todoInfo = Tododata.getValue(TodoInfo.class);
                    arrayList.add(todoInfo);
                }
                adapter = new reAdapter(arrayList, getApplicationContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //
        List<EventDay> events = new ArrayList<>();
        databaseReference.child("TodoInfo").child(lec_Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot planDate : snapshot.getChildren()){
                    String date = (String) planDate.getKey();
                    String[] datel = date.split("-");
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, Integer.parseInt(datel[0]));
                    cal.set(Calendar.MONTH, Integer.parseInt(datel[1]) - 1);
                    cal.set(Calendar.DATE, Integer.parseInt(datel[2]));
                    EventDay eventDay = new EventDay(cal, R.drawable.mini_dot);
                    events.add(eventDay);
                }
                calendarView.setEvents(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule2);

        Intent intent = getIntent();
        lec_Uid = intent.getStringExtra("lecUid");

        recyclerView = findViewById(R.id.recyclerview); // 아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //lec 객체를 담을 어레이리스트(어댑터쪽으로 보내기)
        adapter = new reAdapter(arrayList, getApplicationContext());
        recyclerView.setAdapter(adapter);

        database =  FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동하기
        databaseReference =  FirebaseDatabase.getInstance().getReference("SharePlan");//DB테이블 연결

        //
        List<EventDay> events = new ArrayList<>();
        databaseReference.child("TodoInfo").child(lec_Uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot planDate : snapshot.getChildren()){
                    String date = (String) planDate.getKey();
                    String[] datel = date.split("-");
                    Calendar cal = Calendar.getInstance();
                    cal.set(Calendar.YEAR, Integer.parseInt(datel[0]));
                    cal.set(Calendar.MONTH, Integer.parseInt(datel[1]) - 1);
                    cal.set(Calendar.DATE, Integer.parseInt(datel[2]));
                    EventDay eventDay = new EventDay(cal, R.drawable.mini_dot);
                    events.add(eventDay);
                }
                calendarView.setEvents(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //

        calendarView = findViewById(R.id.calendar);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                String datekey =  clickedDayCalendar.getTime().getYear()+1900+"-"+(clickedDayCalendar.getTime().getMonth()+1)+"-"+clickedDayCalendar.getTime().getDate();
                databaseReference.child("TodoInfo").child(lec_Uid).child(datekey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayList.clear();
                        Log.d("sc", "size"+snapshot.getChildrenCount());
                        for(DataSnapshot Tododata : snapshot.getChildren()){
                            TodoInfo todoInfo = Tododata.getValue(TodoInfo.class);
                            arrayList.add(todoInfo);
                        }
                        adapter = new reAdapter(arrayList,getApplicationContext());
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        recyclerView.setAdapter(adapter);
    }
}