package com.example.shareplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class CreateLec_2_Activity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lec2);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SharePlan");

        Intent newIntent = getIntent();
        EditText studentsText = findViewById(R.id.studentNumber);

        ListView listView = (ListView) findViewById(R.id.nums_listView);

        CreateLec_2_Activity.ListViewAdapter adapter = new CreateLec_2_Activity.ListViewAdapter(CreateLec_2_Activity.this);
        listView.setAdapter(adapter);

        Button add = findViewById(R.id.addStudent);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stuNum = studentsText.getText().toString();
                mDatabaseRef.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isExist = false;
                        for(DataSnapshot userData : snapshot.getChildren()) {
                            UserInfo userInfo = userData.getValue(UserInfo.class);
                            if(userInfo.getStunum().equals(stuNum)) {
                                adapter.addItem(userInfo);
                                isExist = true;
                                break;
                            }
                        }
                        if(isExist == false) {
                            Toast.makeText(getApplicationContext(), "존재하지 않는 학생입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        Button register = findViewById(R.id.lectureRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<UserInfo> updateNumbers = new ArrayList<>();

                for(int i=0; i<adapter.getCount(); i++) {
                    updateNumbers.add(adapter.getItem(i));
                }

                mDatabaseRef.child("LectureInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<LectureInfo> lectures = new ArrayList<>();

                        for(DataSnapshot lectureData : snapshot.getChildren()) {
                            lectures.add(lectureData.getValue(LectureInfo.class));
                        }

                        LectureInfo newLecture = new LectureInfo();
                        newLecture.setName(newIntent.getStringExtra("Name"));
                        newLecture.setProfessor(newIntent.getStringExtra("Professor"));
                        newLecture.setDay(newIntent.getStringExtra("Day"));
                        newLecture.setDivision(newIntent.getStringExtra("Division"));
                        newLecture.setTime(newIntent.getStringExtra("Time"));
                        lectures.add(newLecture);

                        mDatabaseRef.child("LectureInfo").setValue(lectures);

                        for(UserInfo num : updateNumbers) {
                            mDatabaseRef.child("ClassInfo").child(String.valueOf(lectures.size() - 1)).child(num.getStunum()).setValue(num.getName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                Intent intent = new Intent(CreateLec_2_Activity.this,ClassListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    class StudentNumberItemView extends LinearLayout {
        TextView name;
        TextView number;

        public StudentNumberItemView(Context context) {
            super(context);
            init(context);
        }

        public StudentNumberItemView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public void init(Context context) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.students_item, this, true);

            name = (TextView) findViewById(R.id.student_item_name);
            number = (TextView) findViewById(R.id.student_item_number);
        }

        public void setTitle(String name) {
            this.name.setText(name);
        }

        public void setInfo(String number) {
            this.number.setText(number);
        }
    }

    class ListViewAdapter extends BaseAdapter {

        ArrayList<UserInfo> items = new ArrayList<>();

        public ListViewAdapter(CreateLec_2_Activity createLec_2_activity) {

        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public UserInfo getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            CreateLec_2_Activity.StudentNumberItemView itemView = new CreateLec_2_Activity.StudentNumberItemView(getApplicationContext());

            UserInfo item = items.get(position);
            itemView.setTitle(item.getName());
            String number = item.getStunum();
            itemView.setInfo(number);
            return itemView;
        }

        public void addItem(UserInfo item) {
            items.add(item);
        }

        public void clear() {
            items = new ArrayList<UserInfo>();
        }
    }
}