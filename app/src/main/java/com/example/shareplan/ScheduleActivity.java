package com.example.shareplan;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TodoAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<TodoInfo> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private CalendarView calendarView;
    private Button button;
    private String lec_Uid;
    private int year, month, day;

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
                adapter = new TodoAdapter(arrayList, getApplicationContext());
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        Intent intent = getIntent();
        lec_Uid = intent.getStringExtra("lecUid");

        recyclerView = findViewById(R.id.recyclerview); // 아이디 연결
        recyclerView.setHasFixedSize(true); //리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); //lec 객체를 담을 어레이리스트(어댑터쪽으로 보내기)
        adapter = new TodoAdapter(arrayList, getApplicationContext());
        recyclerView.setAdapter(adapter);
        button = findViewById(R.id.btn_add);
        database =  FirebaseDatabase.getInstance(); //파이어베이스 데이터베이스 연동하기
        databaseReference =  FirebaseDatabase.getInstance().getReference("SharePlan");

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
                        for(DataSnapshot Tododata : snapshot.getChildren()){
                            TodoInfo todoInfo = Tododata.getValue(TodoInfo.class);
                            arrayList.add(todoInfo);
                        }
                        adapter = new TodoAdapter(arrayList, getApplicationContext());
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
                // 현재 달력에 선택된 날짜 intent에 담아 보내기
                Calendar selectedDate = calendarView.getSelectedDate();
                intent1.putExtra("year", selectedDate.getTime().getYear()+1900);
                intent1.putExtra("month", selectedDate.getTime().getMonth());
                intent1.putExtra("day", selectedDate.getTime().getDate());
                intent1.putExtra("lecUid",lec_Uid);
                startActivity(intent1);

            }
        });

        recyclerView.setAdapter(adapter);
    }

    public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

        private ArrayList<TodoInfo> arrayList;
        private Context context;

        public TodoAdapter() {
            this.arrayList = new ArrayList<>();
            this.context = null;
        }

        public TodoAdapter(ArrayList<TodoInfo> arrayList, Context context) {
            this.arrayList = arrayList;
            this.context = context;
        }

        @NonNull
        @Override
        public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
            TodoViewHolder holder = new TodoViewHolder(view);

            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar selectedDate = calendarView.getSelectedDate();
                    String datekey = selectedDate.getTime().getYear()+1900 + "-" + (selectedDate.getTime().getMonth()+1) + "-" + selectedDate.getTime().getDate();
                    databaseReference.child("TodoInfo").child(lec_Uid).child(datekey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            TodoInfo toDelete = arrayList.get(holder.pos);
                            arrayList.clear();
                            for(DataSnapshot Tododata : snapshot.getChildren()){
                                TodoInfo todoInfo = Tododata.getValue(TodoInfo.class);
                                if(toDelete.getTitle().equals(todoInfo.getTitle()) && toDelete.getDate().equals(todoInfo.getDate()) && toDelete.getType().equals(todoInfo.getType())) {
                                    continue;
                                }
                                arrayList.add(todoInfo);
                            }
                            databaseReference.child("TodoInfo").child(lec_Uid).child(datekey).setValue(arrayList);
                            adapter = new TodoAdapter(arrayList, getApplicationContext());
                            recyclerView.setAdapter(adapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {
            holder.lectime.setText(arrayList.get(position).getDate());
            holder.lecsubject.setText(arrayList.get(position).getTitle());
            holder.pos = holder.getAdapterPosition();
        }

        @Override
        public int getItemCount() {
            return (arrayList != null ? arrayList.size() : 0);
        }

        public class TodoViewHolder extends RecyclerView.ViewHolder {
            TextView lectime;
            TextView lecsubject;
            Button delete;
            int pos;

            public TodoViewHolder(@NonNull View itemView) {
                super(itemView);
                this.lectime = itemView.findViewById(R.id.lectime);
                this.lecsubject = itemView.findViewById(R.id.lecsubject);
                this.delete = itemView.findViewById(R.id.deleteButton);
            }
        }
    }
}