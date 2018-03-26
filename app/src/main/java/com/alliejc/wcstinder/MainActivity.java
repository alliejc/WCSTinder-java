package com.alliejc.wcstinder;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivity extends AppCompatActivity {

    public static final String NEWCOMER = "newcomer";
    public static final String NOVICE = "novice";
    public static final String INTERMEDIATE = "intermediate";
    public static final String ADVANCED = "advanced";
    public static final String ALL_STAR = "allstar";

    public static final String LEADER = "leader";
    public static final String FOLLOW = "follow";

    private Toolbar mToolbar;
    private RadioGroup mRadioGroup;
    private RecyclerView mRecyclerView;
    private String mSelectedLevel = NEWCOMER;
    private String mSelectedRole = LEADER;
    private TabLayout mTabLayout;
    private List<Dancer> mDancers;
    private DancerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        mDancers = new ArrayList<>();
        setUpUI();
        setUpToolbar();
        setUpTabs();
        setUpRecyclerView();
        getDancers(mSelectedLevel, mSelectedRole);
        mRadioGroup.check(R.id.newcomer_radio_button);
    }

    private void setUpLoginDialog() {
        android.app.FragmentManager fm = getFragmentManager();
        LoginDialog dialogFragment = new LoginDialog ();
        dialogFragment.show(fm, "Login Dialog");    }

    private void setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

            mAdapter = new DancerAdapter(this, new IOnSelected() {
                @Override
                public void onDancerSelected(String name) {
                    setUpLoginDialog();
                }
            });
            mRecyclerView.setAdapter(mAdapter);
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
                        getDancers(mSelectedLevel, mSelectedRole);
                        break;
                    case R.id.novice_radio_button:
                        mSelectedLevel = NOVICE;
                        getDancers(mSelectedLevel, mSelectedRole);
                        break;
                    case R.id.intermediate_radio_button:
                        mSelectedLevel = INTERMEDIATE;
                        getDancers(mSelectedLevel, mSelectedRole);
                        break;
                    case R.id.advanced_radio_button:
                        mSelectedLevel = ADVANCED;
                        getDancers(mSelectedLevel, mSelectedRole);
                        break;
                    case R.id.allstar_radio_button:
                        mSelectedLevel = ALL_STAR;
                        getDancers(mSelectedLevel, mSelectedRole);
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
                mSelectedRole = LEADER;
                getDancers(mSelectedLevel, mSelectedRole);
                break;
            case 1:
                mSelectedRole = FOLLOW;
                getDancers(mSelectedLevel, mSelectedRole);
                break;
        }
    }

    private void getDancers(String division, String role) {
        Call call = APIService.getAPIService().getAllForDivision(role, division);
        call.enqueue(new Callback<Dancer>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if(response.isSuccessful()) {
                    List l = (List) response.body();
                   mAdapter.updateAdapter(l);
                }
            }

            @Override
            public void onFailure(Call<Dancer> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Oops, there was a problem, check your network connection", Toast.LENGTH_SHORT).show();
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
