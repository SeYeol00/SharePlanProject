package com.example.shareplan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntToDoubleFunction;

public class IntroActivity extends AppCompatActivity {


    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
    private MaterialButton buttonOnboardingAction;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Intent userIntent = getIntent();
        String strEmail = userIntent.getStringExtra("UserEmail");
        String strPwd = userIntent.getStringExtra("UserPwd");
        uid = userIntent.getStringExtra("UserUID");

        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonOnboardingAction = findViewById(R.id.buttonOnboardingAction);

        setupOnboardingItems();

        ViewPager2 onboardingViewPager = findViewById(R.id.onboardingViewPager);
        onboardingViewPager.setAdapter(onboardingAdapter);

        setupOnboardingIndicators();
        setCurrentOnboardingIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });

        buttonOnboardingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                    onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
                } else{
                    Intent intent = new Intent(IntroActivity.this, ClassListActivity.class);
                    intent.putExtra("UserUID", uid);
                    intent.putExtra("UserEmail", strEmail);
                    intent.putExtra("UserPwd", strPwd);
                    startActivity(intent);
                    finish();

                }
            }
        });
    }

    private void setupOnboardingItems() {

        List<OnboardingItem> onboardingItems = new ArrayList<>();

        OnboardingItem pageOne = new OnboardingItem();
        pageOne.setTitle("SharePlan에 오신걸 환영합니다");
        pageOne.setDescription("학생인가요?\n교수인가요?");
        pageOne.setImage(R.drawable.page1);

        OnboardingItem pageTwo = new OnboardingItem();
        pageTwo.setTitle("강의 추가 기능");
        pageTwo.setDescription("학생이라면 수업에 참여 할 수 있고,\n교수라면 수업을 만들 수 있습니다");
        pageTwo.setImage(R.drawable.page2);

        OnboardingItem pageThree = new OnboardingItem();
        pageThree.setTitle("강의추가, 학생추가 기능");
        pageThree.setDescription("강의정보를 입력하고\n참여 할 학생의 학번을 입력해주세요");
        pageThree.setImage(R.drawable.page3);

        OnboardingItem pageFour = new OnboardingItem();
        pageFour.setTitle("일정 추가 기능");
        pageFour.setDescription("원하는 날짜를 선택하고\n 일정을 등록 할 수 있습니다");
        pageFour.setImage(R.drawable.page4);

        OnboardingItem pageFive = new OnboardingItem();
        pageFive.setTitle("즐기세요!");
        pageFive.setDescription("SharePlan의 모든 기능을");
        pageFive.setImage(R.drawable.page5);

        onboardingItems.add(pageOne);
        onboardingItems.add(pageTwo);
        onboardingItems.add(pageThree);
        onboardingItems.add(pageFour);
        onboardingItems.add(pageFive);

        onboardingAdapter = new OnboardingAdapter(onboardingItems);

    }

    private void setupOnboardingIndicators() {
        ImageView[] indicators = new ImageView[onboardingAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(8,0,8,0);
        for (int i = 0; i < indicators.length; i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.onboarding_indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int index){
        int childCount = layoutOnboardingIndicators.getChildCount();
        for(int i = 0; i < childCount; i++){
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if(i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active)
                );
            } else{
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive)
                );
            }
        }
        if(index == onboardingAdapter.getItemCount() - 1) {
            buttonOnboardingAction.setText("Start");
        } else {
            buttonOnboardingAction.setText("Next");
        }
    }
}