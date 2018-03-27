package com.alliejc.wcstinder;

import android.content.pm.PackageManager;
import android.os.Trace;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.Toast;


import com.alliejc.wcstinder.adapter.DancerAdapter;
import com.alliejc.wcstinder.callback.IOnSelected;
import com.alliejc.wcstinder.trackmyswing.APIService;
import com.alliejc.wcstinder.trackmyswing.Dancer;
import com.alliejc.wcstinder.util.CacheUtil;
import com.alliejc.wcstinder.util.DancerCache;
import com.facebook.FacebookSdk;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String NEWCOMER = "newcomer";
    public static final String NOVICE = "novice";
    public static final String INTERMEDIATE = "intermediate";
    public static final String ADVANCED = "advanced";
    public static final String ALL_STAR = "allstar";

    public static final String LEADER = "leader";
    public static final String FOLLOW = "follower";

    private Toolbar mToolbar;
    private RadioGroup mRadioGroup;
    private RecyclerView mRecyclerView;
    private String mSelectedLevel = NEWCOMER;
    private String mSelectedRole = LEADER;
    private TabLayout mTabLayout;
    private List<Dancer> mDancers;
    private DancerAdapter mAdapter;
    private CacheUtil mCacheUtil;
    private boolean mCacheDancersData = false;
    private String mEntryKey = "DancersCache";

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
        mRadioGroup.check(R.id.newcomer_radio_button);
        initCacheProvider();
        getDancers(mSelectedLevel, mSelectedRole);
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
                        getData(false);
                        break;
                    case R.id.novice_radio_button:
                        mSelectedLevel = NOVICE;
                        getData(false);
                        break;
                    case R.id.intermediate_radio_button:
                        mSelectedLevel = INTERMEDIATE;
                        getData(false);
                        break;
                    case R.id.advanced_radio_button:
                        mSelectedLevel = ADVANCED;
                        getData(false);
                        break;
                    case R.id.allstar_radio_button:
                        mSelectedLevel = ALL_STAR;
                        getData(false);
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
                getData(false);
                break;
            case 1:
                mSelectedRole = FOLLOW;
                getData(false);
                break;
        }
    }

    private void getData(Boolean ignoreCacheExpiry){
        List<Dancer> allDancerList = DancerCache.getInstance().getAllDancers();
        if (allDancerList != null && allDancerList.size() > 0) {
            mAdapter.updateAdapter(allDancerList);
        } else  if (mCacheUtil != null){
            allDancerList = mCacheUtil.getDataFromCache(ignoreCacheExpiry, mEntryKey);
            if (allDancerList != null) {
                mAdapter.updateAdapter(allDancerList);
            }
        } else {
            getDancers(mSelectedLevel, mSelectedRole);
        }
    }

    private void initCacheProvider() {
        mCacheUtil = new CacheUtil(getApplicationContext());
        mCacheUtil.initCacheProvider(mEntryKey);
    }

    public void cacheDancers(List<Dancer> drugs) {
        if (drugs != null && mCacheUtil != null) {
            if (mCacheDancersData) {
                mCacheUtil.setExpiryTime(System.currentTimeMillis() + 24 * 60 * 60 * 1000 * 7);     //7days
                mCacheUtil.saveDataToCache(drugs, mEntryKey);
            } else {
                DancerCache.getInstance().setIsDancerReadFromResourceFile(true);
            }
            DancerCache.getInstance().setAllDrugs(drugs);
        }
    }

    private void getDancers(String division, String role) {
        Call call = APIService.getAPIService().getAllForDivision(role, division);
        call.enqueue(new Callback<Dancer>() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if(response.isSuccessful()) {
                    List l = (List) response.body();
                    mCacheDancersData = true;
                    cacheDancers(l);
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
