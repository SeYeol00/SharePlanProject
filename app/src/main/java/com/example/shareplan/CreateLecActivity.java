package com.example.shareplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateLecActivity extends AppCompatActivity {

    private DatabaseReference mDatabaseRef;
    private EditText EtLecName, EtLecProfessor, EtLecDivision; //Lecture input
    private CheckBox ChMon, ChTue, ChWends, ChThurs, ChFri;
    private TimePicker TpStart,TpEnd; // TimePicker
    private Button BtRegister; //LecRegister button
    String StartTime;
    String EndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lec);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference("SharePlan");
//edittext
        EtLecName = findViewById(R.id.et_lec_name);
        EtLecProfessor = findViewById(R.id.et_lec_professor);
        EtLecDivision = findViewById(R.id.et_lec_division);
//checkbox
        ChMon = findViewById(R.id.ch_mon);
        ChTue = findViewById(R.id.ch_tue);
        ChWends = findViewById(R.id.ch_wends);
        ChThurs = findViewById(R.id.ch_thurs);
        ChFri = findViewById(R.id.ch_fri);
//timepicker
        TpStart = findViewById(R.id.tp_start);
        TpEnd = findViewById(R.id.tp_end);
//button
        BtRegister = findViewById(R.id.bt_lec_register);

        BtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strLecName = EtLecName.getText().toString();
                String strLecProfessor = EtLecProfessor.getText().toString();
                String strLecDivision = EtLecDivision.getText().toString();

                String strLecDay = "";
                if(ChMon.isChecked() == true) strLecDay += ChMon.getText().toString();
                if(ChTue.isChecked() == true) strLecDay += ChTue.getText().toString();
                if(ChWends.isChecked() == true) strLecDay += ChWends.getText().toString();
                if(ChThurs.isChecked() == true) strLecDay += ChThurs.getText().toString();
                if(ChFri.isChecked() == true) strLecDay += ChFri.getText().toString();

                if(strLecName.equals("")) {
                    Toast.makeText(CreateLecActivity.this, "강의 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strLecProfessor.equals("")) {
                    Toast.makeText(CreateLecActivity.this, "교수님의 이름을 입력헤주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strLecDivision.equals("")) {
                    Toast.makeText(CreateLecActivity.this, "분반을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(strLecDay.equals("")) {
                    Toast.makeText(CreateLecActivity.this, "강의 요일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String stHour = String.format("%02d", TpStart.getHour());
                if (TpStart.getHour() % 12 == 0) {
                    stHour = String.format("%02d", TpStart.getHour() + 12);
                }
                String stMinutes = String.format("%02d", TpStart.getMinute());
                String StartTime = stHour +":"+ stMinutes;

                String edHour = String.format("%02d", TpEnd.getHour());
                if (TpEnd.getHour() % 12 == 0) {
                    edHour = String.format("%02d", TpEnd.getHour() + 12);
                }
                String edMinutes = String.format("%02d", TpEnd.getMinute());
                String EndTime = edHour +":"+ edMinutes;

                String strLecTime = StartTime + "~" + EndTime;

                Intent intent = new Intent(getApplicationContext(), CreateLec_2_Activity.class);

                intent.putExtra("Name",strLecName);
                intent.putExtra("Professor",strLecProfessor);
                intent.putExtra("Day",strLecDay);
                intent.putExtra("Division",strLecDivision);
                intent.putExtra("Time",strLecTime);
                intent.putExtra("UserUID", getIntent().getStringExtra("UserUID"));

                startActivity(intent);
                finish();
            }
        });
    }

}
