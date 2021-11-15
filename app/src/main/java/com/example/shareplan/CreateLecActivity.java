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
    private EditText EtLecName, EtLecProfessor, EtLecDibision; //Lecture input
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
        EtLecDibision = findViewById(R.id.et_lec_division);
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
        BtRegister = findViewById(R.id.bt_register);

        BtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String strLecName = EtLecName.getText().toString();
                String strLecProfessor = EtLecProfessor.getText().toString();
                String strLecDibision = EtLecDibision.getText().toString();

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
                intent.putExtra("Day",strLecDibision);
                intent.putExtra("Division",strLecDibision);
                intent.putExtra("Time",strLecTime);

                startActivity(intent);

            }
        });

        // xml 파일에는 강의 정보를 받아오기 위한 EditText들이 존재해야함.
        // 아마 분량상 2개의 xml 파일을 써야하지 않을까 싶음
        // 1페이지)
        // 강의 이름, 담당 교수, 분반, 강의 요일, 강의 시간
        // 2페이지)
        // 강의를 수강하는 학생들의 학번들을 입력해야함
        // 강의를 추가할 때, 아무 강의나 추가할 수 없도록 학생들의 학번을 통해 로그인 하기 때문에,
        // 이 정보가 입력되어 있어야 강의에 로그인 할 수 있음
        // 어차피 학번 입력 EditText는 스크롤이 가능하도록 만들어야 할 테니,
        // 하나의 xml 안에 모두 집어넣을 수도 있음 --> 이게 조금 더 데이터를 다루기 편할 것 같기는 함.

        // 1. 1페이지에서 2페이지로 넘어가기 위한 버튼이 있어야됨
        // 버튼 클릭 시, onClickListener를 이용하여
        // onCreate 맨 위에 있는 setContentView를 통해 layout을 변경해 주면 될 것 같음.

        // 2. 2페이지에서 버튼 클릭 시, 강의 데이터들을 데이터베이스에 기록하여야 함.
        // 데이터 읽기 및 쓰기의 자세한 방법은 아래 링크 참고
        // https://firebase.google.com/docs/database/android/read-and-write?hl=ko#java_4
        // 데이터를 쓸 때에 생성된 각 강의의 UID를 통해 수강 인원 또한 데이터베이스에 등록하여야 함.
        // 생성된 강의 채널 UID를 읽어와서, 수강 인원 데이터베이스에 해당 UID 값으로 트리를 생성한 후,
        // 해당 UID 트리 아래에 학번 데이터들을 기록함.
    }

}
