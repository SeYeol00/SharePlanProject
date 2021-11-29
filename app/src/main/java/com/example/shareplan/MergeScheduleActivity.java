package com.example.shareplan;

import android.content.Intent;
import android.media.metrics.Event;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MergeScheduleActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TodoInfo> todoList;
    private ArrayList<String> lecIdList;
    private DatabaseReference userLecRef;
    private DatabaseReference todoRef;
    private CalendarView calendarView;
    private Button button;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule2);

        recyclerView = findViewById(R.id.recyclerview); // 아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        button = findViewById(R.id.btn_add);
        todoList = new ArrayList<>(); // todoInfo 객체를 담을 리스트
        lecIdList = new ArrayList<>();  // lec id 를 담을 리스트
        todoRef =  FirebaseDatabase.getInstance().getReference("SharePlan");
        userLecRef = FirebaseDatabase.getInstance().getReference("SharePlan");
        calendarView = findViewById(R.id.calendar);

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        List<EventDay> events = new ArrayList<>();
        // 현재 유저가 참여하고 있는 모든 강의 채널 id를 모아 리스트로 만들기 -- lecIdList
        userLecRef.child("UserLectureInfo").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lecIdList.clear();
                for(DataSnapshot lecId : snapshot.getChildren()) {
                    lecIdList.add((String)lecId.getValue());
                }
                
                // lecIdList가 다 만들어지면
                events.clear();
                // 일정이 있는 날짜에 점 표시
                for (String lec_Uid : lecIdList) {
                    todoRef.child("TodoInfo").child(lec_Uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot planDate : snapshot.getChildren()) {
                                String date = (String) planDate.getKey();
                                // ex) date : "2021-11-13"
                                String[] datel = date.split("-");
                                // 일정 날짜의 calendar 객체 생성
                                Calendar cal = Calendar.getInstance();
                                cal.set(Calendar.YEAR, Integer.parseInt(datel[0]));
                                cal.set(Calendar.MONTH, Integer.parseInt(datel[1]) - 1);
                                cal.set(Calendar.DATE, Integer.parseInt(datel[2]));
                                // calendar 객체와 dot 이미지를 이용해 eventday 객체 생성
                                EventDay eventDay = new EventDay(cal, R.drawable.mini_dot);
                                events.add(eventDay);
                            }
                            // calendarView에 적용
                            calendarView.setEvents(events);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Calendar selectedDate = calendarView.getSelectedDate();
        String datekey = selectedDate.getTime().getYear()+1900 + "-" + (selectedDate.getTime().getMonth()+1) + "-" + selectedDate.getTime().getDate();

        userLecRef.child("UserLectureInfo").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                todoList.clear();
                for (String lec_Uid : lecIdList) {
                    todoRef.child("TodoInfo").child(lec_Uid).child(datekey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot Tododata : snapshot.getChildren()) {
                                TodoInfo todoInfo = Tododata.getValue(TodoInfo.class);
                                todoList.add(todoInfo);
                            }
                            adapter = new reAdapter(todoList, getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // calendarView 에서 날짜가 선택됐을 경우
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                String datekey =  clickedDayCalendar.getTime().getYear()+1900+"-"+(clickedDayCalendar.getTime().getMonth()+1)+"-"+clickedDayCalendar.getTime().getDate();
                todoList.clear();
                for (String lec_Uid : lecIdList) {
                    todoRef.child("TodoInfo").child(lec_Uid).child(datekey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot Tododata : snapshot.getChildren()) {
                                TodoInfo todoInfo = Tododata.getValue(TodoInfo.class);
                                todoList.add(todoInfo);
                            }
                            adapter = new reAdapter(todoList, getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });
    }
}