package com.alliejc.wcstinder;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final int NEWCOMER = 0;
    public static final int NOVICE = 1;
    public static final int INTERMEDIATE = 2;
    public static final int ADVANCED = 3;
    public static final int ALL_STAR = 4;

    private Toolbar mToolbar;
    private RadioGroup mRadioGroup;
    private RecyclerView mRecyclerView;
    private String mSelectedLevel;
    private TabLayout mTabLayout;
    private List<Object> mDancers;
    private DancerAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDancers = new ArrayList<>();
        setUpUI();
        setUpToolbar();
        setUpTabs();
        setUpRecyclerView();
        getTabData(mTabLayout.getSelectedTabPosition());
    }

    private void setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
//        mRecyclerView.setHasFixedSize(true);

        mAdapter = new DancerAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
//        mAdapter.updateAdapter(mDancers);

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
                        mSelectedLevel = "newcomer";
                        break;
                    case R.id.novice_radio_button:
                        mSelectedLevel = "novice";
                        break;
                    case R.id.intermediate_radio_button:
                        mSelectedLevel = "intermediate";
                        break;
                    case R.id.advanced_radio_button:
                        mSelectedLevel = "advanced";
                        break;
                    case R.id.allstar_radio_button:
                        mSelectedLevel = "allstar";
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
                getDancers(mSelectedLevel, "follower");
                break;
            case 1:
                getDancers(mSelectedLevel, "leader");
                break;
        }
    }

    private void getDancers(String division, String role) {
        Call call = APIService.getAPIService().getAllForDivision(role, division);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                if(response.isSuccessful()) {
                    Log.e("SUCCESS", String.valueOf(response.message()));
//                    mDancers.addAll(response.body());
//                    mAdapter.updateAdapter(mDancers);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Log.e("FAILURE", String.valueOf(call));
                Log.e("FAILURE", String.valueOf(t));
                Toast.makeText(MainActivity.this, "Error on response", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowTitleEnabled(false);
            bar.setElevation(2);
        }
    }
}
