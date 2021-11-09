package com.example.shareplan;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class SearchLecActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_lec);

        ListView listView = (ListView) findViewById(R.id.search_listView);

        ListViewAdapter adapter = new ListViewAdapter(SearchLecActivity.this);
        listView.setAdapter(adapter);

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

    // 3. onClickListener 실행 시 사용할 함수 선언 및 구현 ( onClickListener 안에 구현해도 됨 )
    // onCreate 실행 시 실행하던 (1)번 항목과 마찬가지로 FireBase 강의 채널 데이터베이스에서 강의 데이터들을 가져옴
    // 다만, 모든 항목을 가져오는 것이 아니라, 강의 데이터의 "강의 이름" 항목이 탐색 EditText에 적혀있는 문자열을
    // 포함한 경우에만 가져옴
    // Ex) "프로그래밍" 검색 시 --> "모바일프로그래밍", "객체지향프로그래밍", "임베디드프로그래밍", etc...
    // adapter를 초기화 한 후, 데이터베이스에서 가져온 값들을 adapter에 추가함.
    // 마찬가지로 데이터가 갱신됨과 상관 없이 검색하는 순간의 데이터만 가져오면 되므로,
    // get()이나 addListenerForSingleValueEvent를 사용하여 데이터의 값을 가져온다.
}
