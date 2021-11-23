package com.example.shareplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FindPwdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_pwd);

        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference("SharePlan");

        EditText et_stuNum = findViewById(R.id.et_stuNum);
        EditText et_name = findViewById(R.id.et_name);
        EditText et_phone = findViewById(R.id.et_phone);
        EditText et_email = findViewById(R.id.et_email);

        TextView yourPwd = findViewById(R.id.your_pwd);

        Button btn_findEmail = findViewById(R.id.btn_findPwd);
        btn_findEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stuNum = et_stuNum.getText().toString();
                String name = et_name.getText().toString();
                String phone = et_phone.getText().toString();
                String email = et_email.getText().toString();

                mDatabaseRef.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot userData : snapshot.getChildren()) {
                            UserInfo userInfo = userData.getValue(UserInfo.class);
                            if(userInfo.getStunum().equals(stuNum) && userInfo.getName().equals(name) && userInfo.getPhoneNumber().equals(phone) && userInfo.getEmail().equals(email)) {
                                String hiddenPwd = "";
                                for(int i=0; i<userInfo.getPassword().length(); i++) {
                                    if(i<8) {
                                        hiddenPwd += userInfo.getPassword().charAt(i);
                                    } else {
                                        hiddenPwd += "*";
                                    }
                                }
                                yourPwd.setText("당신의 비밀번호는 : " + hiddenPwd);
                                return;
                            }
                        }
                        Toast.makeText(FindPwdActivity.this, "존재하지 않는 계정입니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        TextView gotoLogin = findViewById(R.id.goto_login);
        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}