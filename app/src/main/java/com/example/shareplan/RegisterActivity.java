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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity
{

    private FirebaseAuth mFirebaseAuth; //Firebase access
    private DatabaseReference mDatabaseRef;  //Real time Database
    private EditText EtEmail, EtPw, EtPw2, EtName, EtNumber, EtAddress, EtStunum; //Register input
    private Button BtRegister; //Register button
    private RadioGroup RgAcess;
    private RadioButton RbYes,RbNo;
    private int Authority = 1;

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

                Boolean boolAuthority;
                if (Authority == 1){
                    boolAuthority = true;
                }
                else{
                    boolAuthority = false;
                }

                if(strEmail.length() == 0) {
                    Toast.makeText(RegisterActivity.this,"Email을 입력해주세요", Toast.LENGTH_SHORT).show();
                    EtEmail.requestFocus();
                    return;
                }
                if(strPw.length() == 0 || strPw.length() < 6) {
                    Toast.makeText(RegisterActivity.this,"PW가 너무 짧습니다.", Toast.LENGTH_SHORT).show();
                    EtPw.requestFocus();
                    return;
                }
                if(strPw.equals(strPw2) == false){
                    Toast.makeText(RegisterActivity.this,"PW를 확인해주세요", Toast.LENGTH_SHORT).show();
                    EtPw.requestFocus();
                    return;
                }
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
        });

    }
}