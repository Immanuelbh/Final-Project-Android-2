package maim.com.finalproject.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import maim.com.finalproject.R;
import maim.com.finalproject.adapters.SlideTutorialAdapter;

public class SlideTutorialActivity extends AppCompatActivity {
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    private TextView[] mDots;
    private SlideTutorialAdapter slideTutorialAdapter;

    private Button mNextBtn;
    private Button mBackBtn;
    private int mCurrentPage=0; //the current page
    private boolean flag=false;
    private static final String MY_PREFERENCES = "my_preferences";

    public static final String TAG=SlideTutorialActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slide_tutorial_activity);

        mSlideViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.dotsLayout);

        mNextBtn = findViewById(R.id.next_btn);
        mBackBtn = findViewById(R.id.back_btn);
        slideTutorialAdapter = new SlideTutorialAdapter(this);
        mSlideViewPager.setAdapter(slideTutorialAdapter);

        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);


        Boolean isFirstRun=getSharedPreferences("PREFERENCE",MODE_PRIVATE)
                .getBoolean("isfirstrun",true);
        //Log.d("First Run ...........",isFirstRun+"");
        if(!isFirstRun) {
                Intent intent = new Intent(SlideTutorialActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
        }

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mSlideViewPager.getCurrentItem() + 1);
                if(flag) {
                        Intent intent = new Intent(SlideTutorialActivity.this, MainActivity.class);
                        startActivity(intent);
                        getSharedPreferences("PREFERENCE",MODE_PRIVATE).edit()
                              .putBoolean("isfirstrun",false).apply();
                        //Toast.makeText(SlideTutorialActivity.this, "First Run", Toast.LENGTH_SHORT).show();
                        finish();
                    }
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSlideViewPager.setCurrentItem(mSlideViewPager.getCurrentItem() - 1);
                flag=false;
            }
        });


    }

    /*String prevStarted = "prevStarted";
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!sharedpreferences.getBoolean(prevStarted, false)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();
        } else {
            finish();
        }
    }*/

    //using Heading-count/Desc-count for the dots
    public void addDotsIndicator(int position){

        mDots =new TextView[3];
        mDotLayout.removeAllViews(); //without this line it will
                                     //create infinite number of dots

        for (int i = 0; i <mDots.length ; i++) {
            mDots[i]=new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotLayout.addView(mDots[i]);
        }
        //sets dots color
        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }

    }


    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            if(position==2) {
                flag = true;
            }
            else
                flag=false;
        }
        //gets the position of the selected slide
        @Override
        public void onPageSelected(int position) {

            addDotsIndicator(position);
            mCurrentPage=position;
            if(position==0){
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(false);
                mBackBtn.setVisibility(View.INVISIBLE);

                mNextBtn.setText(getString(R.string.next_btn));
                mBackBtn.setText("");
                flag=false;
            }
            else if(position==mDots.length-1) {
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText(getString(R.string.finish_btn));
                mBackBtn.setText(getString(R.string.back_btn));
            }
            else{
                mNextBtn.setEnabled(true);
                mBackBtn.setEnabled(true);
                mBackBtn.setVisibility(View.VISIBLE);

                mNextBtn.setText(getString(R.string.next_btn));
                mBackBtn.setText(getString(R.string.back_btn));
                flag=false;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}
