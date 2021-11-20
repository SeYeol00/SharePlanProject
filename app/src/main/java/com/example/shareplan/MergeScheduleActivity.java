package com.example.shareplan;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
        setContentView(R.layout.activity_schedule2); // TODO : 일정 합친거 보여주는 액티비티 새로 만들어야 할 듯

        recyclerView = findViewById(R.id.recyclerview); // 아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        todoList = new ArrayList<>(); // 투두 객체를 담을 리스트
        lecIdList = new ArrayList<>();  // lec id 를 담을 리스트
        button = findViewById(R.id.btn_add);

        todoRef =  FirebaseDatabase.getInstance().getReference("SharePlan");
        userLecRef = FirebaseDatabase.getInstance().getReference("SharePlan");

        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        userLecRef.child("UserLectureInfo").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot lecId : snapshot.getChildren()) {
                    lecIdList.add((String) lecId.getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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


        recyclerView.setAdapter(adapter);
    }
}