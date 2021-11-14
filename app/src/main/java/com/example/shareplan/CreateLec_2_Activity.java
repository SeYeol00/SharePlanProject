package com.example.shareplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        Intent intent = getIntent();
        EditText studentsText = findViewById(R.id.studentNumber);

        Button register = findViewById(R.id.lectureRegister);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] studentsNumber = studentsText.getText().toString().split("\n");

                ArrayList<String> updateNumbers = new ArrayList<>(Arrays.asList(studentsNumber));

                mDatabaseRef.child("LectureInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<LectureInfo> lectures = new ArrayList<>();

                        for(DataSnapshot lectureData : snapshot.getChildren()) {
                            lectures.add(lectureData.getValue(LectureInfo.class));
                        }

                        LectureInfo newLecture = new LectureInfo();
                        newLecture.setName(intent.getStringExtra("Name"));
                        newLecture.setProfessor(intent.getStringExtra("Professor"));
                        newLecture.setDay(intent.getStringExtra("Day"));
                        newLecture.setDivision(intent.getStringExtra("Division"));
                        newLecture.setTime(intent.getStringExtra("Time"));
                        lectures.add(newLecture);

                        mDatabaseRef.child("LectureInfo").setValue(lectures);

                        for(String num : updateNumbers) {
                            mDatabaseRef.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot userData : snapshot.getChildren()) {
                                        UserInfo userInfo = userData.getValue(UserInfo.class);
                                        if(userInfo.getStunum().equals(num)) {
                                            String studentName = userInfo.getName();
                                            mDatabaseRef.child("ClassInfo").child(String.valueOf(lectures.size() - 1)).child(num).setValue(studentName);
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }
}