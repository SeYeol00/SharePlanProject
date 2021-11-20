package com.example.shareplan;

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
import android.widget.EditText;
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

public class SearchLecActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef;
    private String uid = "";
    private String stuNum = "";
    private String stuName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lec);

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SharePlan");

        Intent userIntent = getIntent();
        uid = userIntent.getStringExtra("UserUID");

        mDatabaseRef.child("UserInfo").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserInfo userInfo = snapshot.getValue(UserInfo.class);
                stuNum = userInfo.getStunum();
                stuName = userInfo.getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        EditText lecName = findViewById(R.id.lec_name);
        Button search = findViewById(R.id.search);

        ListView listView = (ListView) findViewById(R.id.search_listView);

        ListViewAdapter adapter = new ListViewAdapter(getApplicationContext());
        listView.setAdapter(adapter);

        // 특정 강의 클릭 시 유저별 개인 데이터베이스에 해당 강의의 UID 등록
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 안내메세지 표시
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchLecActivity.this);
                builder.setTitle("확인");
                builder.setMessage("이 강의를 추가하시겠습니까?");
                builder.setIcon(android.R.drawable.ic_dialog_info);

                builder.setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                // 예 버튼 클릭 시 강의 추가
                builder.setNegativeButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 유저강의데이터베이스에 값 추가
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
                                        // UserLectureInfo 트리의 하위 멤버에 선택한 Lecture의 UID를 추가함
                                        ArrayList<String> lecUIDs = new ArrayList<>();
                                        mDatabaseRef.child("ClassInfo").child(lecUID).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                boolean registerLec = false;
                                                for(DataSnapshot userData : snapshot.getChildren()) {
                                                    String findNum = userData.getKey();
                                                    String findName = userData.getValue(String.class);
                                                    if (findNum.equals(stuNum) && findName.equals(stuName)) {
                                                        registerLec = true;
                                                        break;
                                                    }
                                                }
                                                if (registerLec) {
                                                    mDatabaseRef.child("UserLectureInfo").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            // 먼저 기존 강의 UID 리스트를 가져온 뒤, 추가하고자 하는 강의의 UID를 추가하고 덮어쓴다.
                                                            boolean isExist = false;
                                                            for(DataSnapshot lecUIDSet : snapshot.getChildren()) {
                                                                String lecUidExist = lecUIDSet.getValue(String.class);
                                                                lecUIDs.add(lecUidExist);
                                                                if(lecUidExist.equals(lecUID))
                                                                    isExist = true;
                                                            }
                                                            if(isExist) {
                                                                Toast.makeText(SearchLecActivity.this, "이미 등록한 강의입니다.", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                lecUIDs.add(lecUID);
                                                                mDatabaseRef.child("UserLectureInfo").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(lecUIDs);
                                                                finish();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText(SearchLecActivity.this, "수강중인 강의가 아닙니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // 검색 버튼 클릭 시 강의명에 검색 키워드가 포함되어 있는 강의들만 리스트뷰에 추가함
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.clear();
                String name = lecName.getText().toString();
                mDatabaseRef.child("LectureInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot lectureData : snapshot.getChildren()) {
                            LectureInfo lecture = lectureData.getValue(LectureInfo.class);
                            if(lecture.getName().contains(name)) {
                                adapter.addItem(lecture);
                            }
                        }
                        listView.setAdapter(adapter);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        // 초기 화면으로는 모든 강의 리스트를 리스트뷰에 추가하여 보여줌
        mDatabaseRef.child("LectureInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adapter.clear();
                for(DataSnapshot lectureData : snapshot.getChildren()) {
                    LectureInfo lecture = lectureData.getValue(LectureInfo.class);
                    adapter.addItem(lecture);
                }
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // onCreate에 구현해야 할 기능
        // 1. SearchLecActivity 첫 생성시 기본적으로 FireBase 강의 데이터베이스에 있는 모든 강의 리스트들을 가져와
        // ListView의 adapter에 추가한다.
        // 계속적으로 갱신해야 할 필요는 없으니, addValueEventListener가 아닌
        // get()이나 addListenerForSingleValueEvent를 사용하여 데이터의 값을 가져온다.
        // 데이터 읽기 및 쓰기의 자세한 방법은 아래 링크 참고
        // https://firebase.google.com/docs/database/android/read-and-write?hl=ko#java_4

        // 데이터베이스가 없을 때에 ListView가 제대로 작동하는지 확인하기 위해 아래의 코드를 추가해 둘 수 있지만,
        // ListView가 잘 작동됨을 확인하면 사용하게끔 유지해두면 안되는 코드임
        // adapter.addItem("모바일 프로그래밍", "이창우/2분반/월수 10:30-12:00");

        // 2. "찾기" 버튼 클릭 시 관련된 코드를 실행하기 위한 onClickListener 생성

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

    // 3. onClickListener 실행 시 사용할 함수 선언 및 구현 ( onClickListener 안에 구현해도 됨 )
    // onCreate 실행 시 실행하던 (1)번 항목과 마찬가지로 FireBase 강의 채널 데이터베이스에서 강의 데이터들을 가져옴
    // 다만, 모든 항목을 가져오는 것이 아니라, 강의 데이터의 "강의 이름" 항목이 탐색 EditText에 적혀있는 문자열을
    // 포함한 경우에만 가져옴
    // Ex) "프로그래밍" 검색 시 --> "모바일프로그래밍", "객체지향프로그래밍", "임베디드프로그래밍", etc...
    // adapter를 초기화 한 후, 데이터베이스에서 가져온 값들을 adapter에 추가함.
    // 마찬가지로 데이터가 갱신됨과 상관 없이 검색하는 순간의 데이터만 가져오면 되므로,
    // get()이나 addListenerForSingleValueEvent를 사용하여 데이터의 값을 가져온다.
}
