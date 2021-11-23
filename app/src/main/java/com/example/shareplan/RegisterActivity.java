package com.example.shareplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity
{

    private FirebaseAuth mFirebaseAuth; //Firebase access
    private DatabaseReference mDatabaseRef;  //Real time Database
    private EditText EtEmail, EtPw, EtPw2, EtName, EtNumber, EtAddress, EtStunum; //Register input
    private Button BtRegister; //Register button
    private RadioGroup RgAcess;
    private RadioButton RbYes,RbNo;
    private int Authority = 1;
    private Boolean boolAuthority = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SharePlan");

        EtEmail = findViewById(R.id.r_et_email);
        EtPw = findViewById(R.id.r_et_pw);
        EtPw2 = findViewById(R.id.r_et_pw2);
        EtName = findViewById(R.id.et_name);
        EtNumber = findViewById(R.id.et_phonenumber);
        EtAddress = findViewById(R.id.et_address);
        EtStunum = findViewById(R.id.et_stunum);

        RgAcess = findViewById(R.id.rg_access);
        RbYes = findViewById(R.id.rb_yes);
        RbNo = findViewById(R.id.rb_no);

        RgAcess.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.rb_yes){
                    Authority = 1;
                }else if(i == R.id.rb_no){
                    Authority = 0;
                }
            }
        });

        BtRegister = findViewById(R.id.r_bt_register);

        BtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // when click on register button
                String strEmail = EtEmail.getText().toString();
                String strPw = EtPw.getText().toString();
                String strPw2 = EtPw2.getText().toString();
                String strName = EtName.getText().toString();
                String strStunum = EtStunum.getText().toString();
                String strNumber = EtNumber.getText().toString();
                String strAddress = EtAddress.getText().toString();

                if (Authority == 1){
                    boolAuthority = true;
                }
                else{
                    boolAuthority = false;
                }

                // 가입시 유효성 검사
                if(strEmail.equals("") || strPw.equals("") || strPw2.equals("") || strName.equals("") || strStunum.equals("") || strNumber.equals("") || strAddress.equals("") || boolAuthority == null) {
                    Toast.makeText(RegisterActivity.this, "모든 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Pattern.matches("\\w+@\\w+\\.\\w+(\\.\\w+)?", strEmail)) {
                    Toast.makeText(RegisterActivity.this, "올바른 이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Pattern.matches("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", strNumber)) {
                    Toast.makeText(RegisterActivity.this, "올바른 휴대전화 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Pattern.matches("^[가-힣]*$", strName)) {
                    Toast.makeText(RegisterActivity.this, "올바른 형태의 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Pattern.matches("^(?=.*[a-zA-Z])(?=.*[!@#$%^~*+=-])(?=.*[0-9]).{10,20}$", strPw)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호는 알파벳, 숫자, 특수문자를 최소 한개 이상씩 포함해주어야 합니다.", Toast.LENGTH_SHORT).show();
                    Toast.makeText(RegisterActivity.this, "비밀번호는 10자 이상, 20자 이하로 작성하여야합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!Pattern.matches("^(?=.*[0-9]).{8,8}$", strStunum)) {
                    Toast.makeText(RegisterActivity.this, "올바른 학번을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!strPw.equals(strPw2)) {
                    Toast.makeText(RegisterActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 가입시 중복 검사
                mDatabaseRef.child("UserInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean isExist = false;
                        for(DataSnapshot userData : snapshot.getChildren()) {
                            UserInfo userInfo = userData.getValue(UserInfo.class);
                            if(userInfo.getName().equals(strEmail)) {
                                Toast.makeText(RegisterActivity.this, "이미 존재하는 이메일 주소입니다.", Toast.LENGTH_SHORT).show();
                                isExist = true;
                                break;
                            }
                            if(userInfo.getStunum().equals(strStunum)) {
                                Toast.makeText(RegisterActivity.this, "이미 가입된 학번입니다.", Toast.LENGTH_SHORT).show();
                                isExist = true;
                                break;
                            }
                            if(userInfo.getPhoneNumber().equals(strNumber)) {
                                Toast.makeText(RegisterActivity.this, "이미 가입된 번호입니다.", Toast.LENGTH_SHORT).show();
                                isExist = true;
                                break;
                            }
                        }
                        if(!isExist) {
                            //Firebase Auth access
                            mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPw).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
                                        UserInfo register = new UserInfo();
                                        register.setId(firebaseUser.getUid());
                                        register.setEmail(firebaseUser.getEmail());
                                        register.setPassword(strPw);
                                        register.setName(strName);
                                        register.setStunum(strStunum);
                                        register.setPhoneNumber(strNumber);
                                        register.setAddress(strAddress);
                                        register.setAuthority(boolAuthority);

                                        mDatabaseRef.child("UserInfo").child(firebaseUser.getUid()).setValue(register);

                                        Toast.makeText(RegisterActivity.this, "register success",Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
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