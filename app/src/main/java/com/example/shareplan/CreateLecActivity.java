package com.example.shareplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;

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

        TpStart.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                String hour = String.valueOf(i);
                String minutes = String.valueOf(i1);
                StartTime = hour +":"+ minutes;
            }
        });
        TpEnd.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int i, int i1) {
                String hour = String.valueOf(i);
                String minutes = String.valueOf(i1);
                EndTime = hour +":"+ minutes;
            }
        });
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

                String strLecTime = StartTime + "~" + EndTime;

                Intent intent = new Intent(getApplicationContext(), CreateLec_2_Activity.class);

                intent.putExtra("Name",strLecName);
                intent.putExtra("Professor",strLecProfessor);
                intent.putExtra("Day",strLecDay);
                intent.putExtra("Division",strLecDivision);
                intent.putExtra("Time",strLecTime);

                startActivity(intent);
                finish();
            }
        });
    }

}
