package com.example.shareplan;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ClassListActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef;
    private ListView listView;
    private ListViewAdapter adapter;
    private String uid;

    @Override
    protected void onResume() {
        super.onResume();
        mDatabaseRef.child("UserLectureInfo").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            // 유저별 강의 목록을 리스트뷰에 저장
            // 데이터베이스가 업데이트 될 때마다 실시간으로 새로고침함
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clear();
                for(DataSnapshot lectureUIDSet : snapshot.getChildren()) {
                    String lectureUID = lectureUIDSet.getValue(String.class);
                    mDatabaseRef.child("LectureInfo").child(lectureUID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            adapter.addItem(snapshot.getValue(LectureInfo.class));
                            listView.setAdapter(adapter);
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        Intent userIntent = getIntent();
        String strEmail = userIntent.getStringExtra("UserEmail");
        String strPwd = userIntent.getStringExtra("UserPwd");
        uid = userIntent.getStringExtra("UserUID");

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SharePlan");
        listView = (ListView) findViewById(R.id.courseListView);

        adapter = new ListViewAdapter(getApplicationContext());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() { // 강의 목록 터치 시
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDatabaseRef.child("LectureInfo").addListenerForSingleValueEvent(new ValueEventListener() { // 해당하는 강의의 uid를 불러옴
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        LectureInfo touchLec = adapter.getItem(position);
                        for(DataSnapshot lectureData : snapshot.getChildren()) {
                            String lecUID = lectureData.getKey();
                            LectureInfo lecture = lectureData.getValue(LectureInfo.class);
                            if(lecture.getName().equals(touchLec.getName()) && lecture.getProfessor().equals(touchLec.getProfessor()) &&
                                    lecture.getDivision().equals(touchLec.getDivision()) && lecture.getDay().equals(touchLec.getDay()) &&
                                    lecture.getTime().equals(touchLec.getTime())) { // 데이터베이스에서 터치한 강의와 일치하는 데이터를 탐색
                                mDatabaseRef.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() { // 일치한다면 현재 로그인한 유저의 권한 가져옴
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot userData : snapshot.getChildren()) {
                                            UserInfo userInfo = userData.getValue(UserInfo.class);
                                            if(userInfo.getEmail().equals(strEmail) && userInfo.getPassword().equals(strPwd)) {
                                                boolean authority = userInfo.getAuthority();
                                                Intent intent;
                                                if(authority) { // 교수라면
                                                    intent = new Intent(ClassListActivity.this, ScheduleActivity.class);
                                                } else { // 일반 학생이라면
                                                    intent = new Intent(ClassListActivity.this, Schedule2Activity.class);
                                                }
                                                intent.putExtra("lecUid", lecUID);
                                                startActivity(intent);
                                                break;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        Button btn_Add = findViewById(R.id.search);

        btn_Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseRef.child("UserInfo").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserInfo userInfo = snapshot.getValue(UserInfo.class);
                        boolean authority= userInfo.getAuthority();
                        Intent intent;
                        if (authority){
                            intent = new Intent(ClassListActivity.this, CreateLecActivity.class);
                        }
                        else{
                            intent = new Intent(ClassListActivity.this, SearchLecActivity.class);
                        }
                        intent.putExtra("UserUID", uid);
                        startActivity(intent);
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
        ArrayList<LectureInfo> items = null;

        public ListViewAdapter(Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(mContext);
            items = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public LectureInfo getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = mLayoutInflater.inflate(R.layout.lec_item, null);

            TextView lecName = (TextView)view.findViewById(R.id.lec_item_name);
            TextView lecInfo = (TextView)view.findViewById(R.id.lec_item_info);

            LectureInfo item = items.get(position);
            lecName.setText(item.getName());
            String desc = item.getProfessor() + " / " + item.getDivision() + " / " + item.getDay() + " / " + item.getTime();
            lecInfo.setText(desc);

            return view;
        }

        public void addItem(LectureInfo item) {
            items.add(item);
        }

        public void clear() {
            items = new ArrayList<LectureInfo>();
        }
    }
}
