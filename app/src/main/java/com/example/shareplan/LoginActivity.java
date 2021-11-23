package com.example.shareplan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth; //Firebase access
    private DatabaseReference mDatabaseRef;  //Real time Database
    private EditText EtEmail, EtPw; //login input


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SharePlan");

        EtEmail = findViewById(R.id.et_email);
        EtPw = findViewById(R.id.et_pw);

        Button bt_login = findViewById(R.id.bt_login);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //click on login button
                String strEmail = EtEmail.getText().toString();
                String strPw = EtPw.getText().toString();

                if(strEmail.length() == 0) {
                    Toast.makeText(LoginActivity.this,"Email을 입력해주세요", Toast.LENGTH_SHORT).show();
                    EtEmail.requestFocus();

                    return;
                }
                if(strPw.length() == 0) {
                    Toast.makeText(LoginActivity.this,"PW를 입력해주세요", Toast.LENGTH_SHORT).show();
                    EtPw.requestFocus();

                    return;
                }

                mFirebaseAuth.signInWithEmailAndPassword(strEmail,strPw).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //success Login
                            Intent intent = new Intent(LoginActivity.this, ClassListActivity.class);
                            intent.putExtra("UserUID", mFirebaseAuth.getCurrentUser().getUid());
                            intent.putExtra("UserEmail", strEmail);
                            intent.putExtra("UserPwd", strPw);
                            startActivity(intent);
                            finish();
                        } else{
                            //fail Login
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Button bt_register = findViewById(R.id.bt_register);
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //click on register button
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        TextView gotoFindEmail = findViewById(R.id.find_email);
        gotoFindEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindEmailActivity.class);
                startActivity(intent);
            }
        });

        TextView gotoFindPwd = findViewById(R.id.find_pwd);
        gotoFindPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindPwdActivity.class);
                startActivity(intent);
            }
        });
    }
}