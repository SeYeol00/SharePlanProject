package com.example.shareplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CreateLec_2_Activity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private CreateLec_2_Activity.ListViewAdapter adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lec2);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SharePlan");

        Intent newIntent = getIntent();
        EditText studentsText = findViewById(R.id.studentNumber);

        ListView listView = (ListView) findViewById(R.id.all_listView);

        CreateLec_2_Activity.ListViewAdapter adapter = new CreateLec_2_Activity.ListViewAdapter(CreateLec_2_Activity.this);
        listView.setAdapter(adapter);

        mDatabaseRef.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clear();
                for(DataSnapshot userData : snapshot.getChildren()) {
                    UserInfo userInfo = userData.getValue(UserInfo.class);
                    adapter.addItem(userInfo);
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ListView listView2 = (ListView) findViewById(R.id.num_listView);

        adapter2 = new CreateLec_2_Activity.ListViewAdapter(CreateLec_2_Activity.this);
        listView2.setAdapter(adapter2);

        Button search = findViewById(R.id.searchStudent);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clear();
                String stuNum = studentsText.getText().toString();

                mDatabaseRef.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isExist = false;
                        for(DataSnapshot userData : snapshot.getChildren()) {
                            UserInfo userInfo = userData.getValue(UserInfo.class);
                            if(userInfo.getStunum().contains(stuNum)) {
                                adapter.addItem(userInfo);
                                isExist = true;
                            }
                        }
                        if(isExist == false) {
                            Toast.makeText(getApplicationContext(), "존재하지 않는 학생입니다.", Toast.LENGTH_SHORT).show();
                        }
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserInfo addedUser = adapter.getItem(position);
                int length = adapter2.getCount();
                adapter2.addItem(addedUser);
                Set<UserInfo> set = new HashSet<>(adapter2.getItems());
                adapter2 = new CreateLec_2_Activity.ListViewAdapter(CreateLec_2_Activity.this, new ArrayList<UserInfo>(set));
                listView2.setAdapter(adapter2);
                if(length == set.size()) {
                    Toast.makeText(CreateLec_2_Activity.this, "이미 등록된 학생입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button register = findViewById(R.id.lectureRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<UserInfo> updateNumbers = new ArrayList<>();

                for(int i=0; i<adapter2.getCount(); i++) {
                    updateNumbers.add(adapter2.getItem(i));
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

                        String uid = getIntent().getStringExtra("UserUID");

                        mDatabaseRef.child("UserLectureInfo").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                // 먼저 기존 강의 UID 리스트를 가져온 뒤, 추가하고자 하는 강의의 UID를 추가하고 덮어쓴다.
                                ArrayList<String> lecUIDs = new ArrayList<>();
                                for(DataSnapshot lecUIDSet : snapshot.getChildren()) {
                                    lecUIDs.add(lecUIDSet.getValue(String.class));
                                }
                                lecUIDs.add(String.valueOf(lectures.size() - 1));
                                mDatabaseRef.child("UserLectureInfo").child(uid).setValue(lecUIDs);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    class ListViewAdapter extends BaseAdapter {

        Context mContext = null;
        LayoutInflater mLayoutInflater = null;
        ArrayList<UserInfo> items = null;

        public ListViewAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
            items = new ArrayList<>();
        }

        public ListViewAdapter(Context context, ArrayList<UserInfo> list) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
            items = list;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public UserInfo getItem(int position) {
            return items.get(position);
        }

        public ArrayList<UserInfo> getItems() {
            return items;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = mLayoutInflater.inflate(R.layout.students_item, null);

            TextView stuName = (TextView)view.findViewById(R.id.student_item_name);
            TextView stuNum = (TextView)view.findViewById(R.id.student_item_number);

            UserInfo item = items.get(position);
            stuName.setText(item.getName());
            String desc = item.getStunum();
            stuNum.setText(desc);

            return view;
        }

        public void addItem(UserInfo item) {
            items.add(item);
        }

        public void clear() {
            items = new ArrayList<UserInfo>();
        }
    }
}