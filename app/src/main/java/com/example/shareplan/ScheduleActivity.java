package com.example.shareplan;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class ScheduleActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private reAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TodoInfo> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private CalendarView calendarView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Intent intent = getIntent();
        String lec_Uid = intent.getStringExtra("lecUid");

        recyclerView = findViewById(R.id.recyclerview); // 아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //lec 객체를 담을 어레이리스트(어댑터쪽으로 보내기)
        button = findViewById(R.id.btn_add);

        database =  FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동하기

        databaseReference = database.getReference("TodoInfo"); //DB테이블 연결
        calendarView = findViewById(R.id.calendar);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {


                Calendar clickedDayCalendar = eventDay.getCalendar();
                String datekey =  clickedDayCalendar.getTime().getYear()+1900+"-"+(clickedDayCalendar.getTime().getMonth()+1)+"-"+clickedDayCalendar.getTime().getDate();
                databaseReference.child(lec_Uid).child(datekey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayList.clear();
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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ScheduleActivity.this, AddscheduleActivity.class);
                intent1.putExtra("lecUid",lec_Uid);
                startActivity(intent1);

            }
        });


        recyclerView.setAdapter(adapter);
    }
}