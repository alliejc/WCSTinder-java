package com.alliejc.wcstinder;

import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.RadioGroup;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    public static final int NEWCOMER = 0;
    public static final int NOVICE = 1;
    public static final int INTERMEDIATE = 2;
    public static final int ADVANCED = 3;
    public static final int ALL_STAR = 4;

    private Toolbar mToolbar;
    private RadioGroup mRadioGroup;
    private RecyclerView mRecyclerView;
    private int mSelectedLevel;
    private TabLayout mTabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();
        setUpToolbar();
    }

    private void setUpUI(){
        mToolbar = findViewById(R.id.toolbar);
        mRadioGroup = findViewById(R.id.level_radio_group);
        mRecyclerView = findViewById(R.id.recycler_view);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.newcomer_radio_button:
                        mSelectedLevel = NEWCOMER;
                        break;
                    case R.id.novice_radio_button:
                        mSelectedLevel = NOVICE;
                        break;
                    case R.id.intermediate_radio_button:
                        mSelectedLevel = INTERMEDIATE;
                        break;
                    case R.id.advanced_radio_button:
                        mSelectedLevel = ADVANCED;
                        break;
                    case R.id.allstar_radio_button:
                        mSelectedLevel = ALL_STAR;
                        break;
                }
            }
        });
    }

    private void setUpTabs(){
        mTabLayout = (TabLayout) findViewById(R.id.role_tablayout);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getTabData(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                getTabData(tab.getPosition());
            }
        });
    }

    private void getTabData(int tabPosition){
        switch (tabPosition){
            case 0:
                getLeaders();
                break;
            case 1:
                getFollowers();
                break;
        }
    }

    private void getFollowers() {

    }

    private void getLeaders() {
    }

//    Call call = APIService.get.getArticles();
//        call.enqueue(new Callback<Article>() {
//        @Override
//        public void onResponse(@NonNull Call<Article> call, @NonNull Response<Article> response) {
//            if (response.isSuccessful()) {
//                mArticleDatumList.addAll(response.body().getData());
//                startArticlesFragment();
//            } else {
//                Toast.makeText(MainActivity.this, "Error on response", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        @Override
//        public void onFailure(Call<Article> call, Throwable t) {
//            Log.e("on failure", call.toString() + "--" + t.toString() + "--"+ t.getMessage());
//        }
//    });
}

    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowTitleEnabled(false);
            bar.setElevation(2);
        }
    }
}
