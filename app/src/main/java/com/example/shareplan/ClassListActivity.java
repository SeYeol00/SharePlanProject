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
    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseRef;
    private ListViewAdapter mListViewAdapter;
    private FirebaseUser mUser;
    private ArrayList<String> lectureArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);



        mFirebaseAuth =FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SharePlan");
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        ListView listView = (ListView) findViewById(R.id.courseListView);

        ListViewAdapter adapter = new ListViewAdapter(ClassListActivity.this);
        listView.setAdapter(adapter);
        String uid = mUser.getUid();
        mDatabaseRef.child("UserLectureInfo").child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lectureArray.clear();
                for(DataSnapshot lectureUIDSet : snapshot.getChildren()) {
                    String lectureUID = lectureUIDSet.getKey();
                    mDatabaseRef.child("LectureInfo").child(lectureUID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            adapter.addItem(snapshot.getValue(LectureInfo.class));
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





        // 현재 로그인된 유저의 UID는 로그인시 Intent를 통해서 넘어옴.
        // 유저별 강의 데이터베이스에서, 로그인한 유저의 UID를 통해 유저가 가지고 있는 강의 UID들을 가져옴.
        // 강의 데이터베이스에서, 유저가 가지고있는 강의 UID를 통해 각 강의의 정보를 가져옴.
        // 각 강의의 정보들을 다듬어서, ListView의 adapter에 추가해줌.
        // 이 목록은, 유저의 강의 목록이 업데이트 될 때마다 계속 갱신되어야 하므로,
        // get()이나 addListenerForSingleValueEvent가 아니라, addValueEventListener를 사용하는 것이 좋음.
        // 데이터 읽기 및 쓰기의 자세한 방법은 아래 링크 참고
        // https://firebase.google.com/docs/database/android/read-and-write?hl=ko#java_4

        // "+" 버튼 클릭 시, 강의 추가 액티비티로 넘어가도록 onClickListener를 구현해야함.
        // 강의 추가/생성 자체는 강의 추가/생성 액티비티에서 이루어지므로, 콜백 함수는 구현하지 않아도 됨.
        // 다만, 유저의 UID를 통해 권한을 받아와야 함.
        // 받아온 권한에 따라, SearchLec으로 보내야 할지, CreateLec으로 보내야 할지를 결정.
        // 이 때에는 버튼이 클릭될 시에만 실행되어야 하므로,
        // get()이나, addListenerForSingleValueEvent를 사용하는 것이 좋음.
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDatabaseRef.child("LectureInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        LectureInfo touchLec = adapter.getItem(position);
                        for(DataSnapshot lectureData : snapshot.getChildren()) {
                            String lecUID = lectureData.getKey();
                            LectureInfo lecture = lectureData.getValue(LectureInfo.class);
                            if(lecture.getName().equals(touchLec.getName()) && lecture.getProfessor().equals(touchLec.getProfessor()) &&
                                    lecture.getDivision().equals(touchLec.getDivision()) && lecture.getDay().equals(touchLec.getDay()) &&
                                    lecture.getTime().equals(touchLec.getTime())) {
                                Intent intent = new Intent(ClassListActivity.this,ScheduleActivity.class);
                                intent.putExtra("lecUid", lecUID);
                                startActivity(intent);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        Button btn_Next = findViewById(R.id.search);

        btn_Next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabaseRef.child("UserInfo").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserInfo userInfo = snapshot.getValue(UserInfo.class);
                        boolean authority= userInfo.getAuthority();
                        if (authority){
                            Intent intent = new Intent(ClassListActivity.this,CreateLecActivity.class);
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(ClassListActivity.this,SearchLecActivity.class);
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }

    class LecItemView extends LinearLayout {
        TextView title;
        TextView info;

        public LecItemView(Context context) {
            super(context);
            init(context);
        }

        public LecItemView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context);
        }

        public void init(Context context) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.lec_item, this, true);

            title = (TextView) findViewById(R.id.lec_item_name);
            info = (TextView) findViewById(R.id.lec_item_info);
        }

        public void setTitle(String name) {
            title.setText(name);
        }

        public void setInfo(String desc) {
            info.setText(desc);
        }
    }
    class ListViewAdapter extends BaseAdapter {

        ArrayList<LectureInfo> items = new ArrayList<LectureInfo>();

        public ListViewAdapter(ClassListActivity ClassListActivity) {

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
            LecItemView itemView = new LecItemView(getApplicationContext());

            LectureInfo item = items.get(position);
            itemView.setTitle(item.getName());
            String desc = item.getProfessor() + " / " + item.getDivision() + " / " + item.getDay() + " / " + item.getTime();
            itemView.setInfo(desc);
            return itemView;
        }

        public void addItem(LectureInfo item) {
            items.add(item);
        }

        public void clear() {
            items = new ArrayList<LectureInfo>();
        }
    }
}
