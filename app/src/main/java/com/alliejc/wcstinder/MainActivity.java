package com.alliejc.wcstinder;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.alliejc.wcstinder.callback.ICallback;
import com.alliejc.wcstinder.callback.IOnSelected;
import com.alliejc.wcstinder.trackmyswing.APIService;
import com.alliejc.wcstinder.trackmyswing.Dancer;
import com.alliejc.wcstinder.util.Constants;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.FacebookSdk;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.alliejc.wcstinder.util.Constants.ADVANCED;
import static com.alliejc.wcstinder.util.Constants.ALL_STAR;
import static com.alliejc.wcstinder.util.Constants.FACEBOOK_URL;
import static com.alliejc.wcstinder.util.Constants.FOLLOW;
import static com.alliejc.wcstinder.util.Constants.INTERMEDIATE;
import static com.alliejc.wcstinder.util.Constants.LEADER;
import static com.alliejc.wcstinder.util.Constants.NEWCOMER;
import static com.alliejc.wcstinder.util.Constants.NOVICE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class MainActivity extends AppCompatActivity implements ICallback{

    public static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private RadioGroup mRadioGroup;
    private RecyclerView mRecyclerView;
    private String mSelectedLevel = NEWCOMER;
    private String mSelectedRole = LEADER;
    private TabLayout mTabLayout;
    private List<Dancer> mDancers;
    private DancerAdapter mAdapter;
    private String mSearchName = "";
    private AccessToken mAccessToken;
    private AccessTokenTracker mAccessTokenTracker;
    private ClipboardManager myClipboard;
    private ClipData mClipData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);


        mAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                mAccessToken = currentAccessToken;
            }
        };

        mAccessTokenTracker.startTracking();

        mDancers = new ArrayList<>();
        setUpUI();
        setUpToolbar();
        setUpTabs();
        setUpRecyclerView();
        mRadioGroup.check(R.id.newcomer_radio_button);
        getDancers(mSelectedLevel, mSelectedRole);
    }

    private void setUpLoginDialog() {
        mAccessToken = AccessToken.getCurrentAccessToken();

        if (mAccessToken == null || mAccessToken.isExpired()) {
            android.app.FragmentManager fm = getFragmentManager();
            LoginDialog dialogFragment = new LoginDialog();
            dialogFragment.show(fm, "Login Dialog");
        } else {
            FACEBOOK_URL = getFacebookSearchURL(mSearchName);
            launchFBIntent();
        }
    }

    private void setUpRecyclerView() {
        mRecyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

            mAdapter = new DancerAdapter(this, new IOnSelected() {
                @Override
                public void onDancerSelected(String name) {
                    mSearchName = name;
                    mClipData = ClipData.newPlainText("search_name", name);
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

    //TODO: implement cache headers
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
//                Toast.makeText(MainActivity.this, "Oops, there was a problem, check your network connection", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowTitleEnabled(true);
            bar.setTitle(R.string.toolbar_title);
            bar.setElevation(2);
        }
    }

    private void launchFBIntent(){
        if(FACEBOOK_URL.equalsIgnoreCase("fb://search/")) {
            myClipboard.setPrimaryClip(mClipData);
            Toast.makeText(getApplicationContext(), mSearchName + " " + "saved to clipboard", Toast.LENGTH_LONG).show();
        }

        FACEBOOK_URL = getFacebookSearchURL(mSearchName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(FACEBOOK_URL));
        startActivity(intent);
    }

    private String getFacebookSearchURL(String searchName) {
        PackageManager packageManager = getApplicationContext().getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            return "fb://search/";

        } catch (PackageManager.NameNotFoundException e) {
            return  "https://www.facebook.com/search/people/?q=" + searchName; //normal web url
        }
    }

    @Override
    public void onError(Object obj) {
    }

    @Override
    public void onCompleted(Object obj) {
        mAccessToken = (AccessToken) obj;
        FACEBOOK_URL = getFacebookSearchURL(mSearchName);
        launchFBIntent();
    }
}
