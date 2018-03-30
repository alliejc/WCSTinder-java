//package com.alliejc.wcstinder
//
//import android.content.ClipData
//import android.content.ClipboardManager
//import android.content.Context
//import android.os.Bundle
//import android.os.PersistableBundle
//import android.support.design.widget.TabLayout
//import android.support.v7.app.AppCompatActivity
//import android.support.v7.widget.LinearLayoutManager
//import android.support.v7.widget.RecyclerView
//import android.support.v7.widget.Toolbar
//import android.widget.RadioGroup
//import com.alliejc.wcstinder.adapter.DancerAdapter
//import com.alliejc.wcstinder.callback.IOnSelected
//import com.alliejc.wcstinder.trackmyswing.APIService
//import com.alliejc.wcstinder.trackmyswing.Dancer
//import com.alliejc.wcstinder.util.Constants.*
//import com.facebook.AccessToken
//import com.facebook.AccessTokenTracker
//import com.facebook.FacebookSdk
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import kotlin.reflect.KProperty
//
///**
// * Created by acaldwell on 3/29/18.
// */
//class MainActivityK : AppCompatActivity() {
//    val TAG = MainActivity::class.java.simpleName
//
//    private val mToolbar:Toolbar by lazy{ findViewById<Toolbar>(R.id.toolbar) }
//    private val mRadioGroup:RadioGroup by lazy{ findViewById<RadioGroup>(R.id.level_radio_group)}
//    private var mRecyclerView:RecyclerView by lazy{ findViewById<RecyclerView>(R.id.recycler_view)}
//    private var mTabLayout:TabLayout by lazy{ findViewById<TabLayout>(R.id.role_tablayout)}
//
//    private var mSelectedLevel = NEWCOMER
//    private var mSelectedRole = LEADER
//    private var mDancers: List<Dancer>? = null
//    private var mAdapter: DancerAdapter? =null
//    private var mSearchName = ""
//    private var mAccessToken: AccessToken? = null
//    private var mAccessTokenTracker: AccessTokenTracker? = null
//
//    private var myClipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//    private var mClipData: ClipData? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        FacebookSdk.sdkInitialize(applicationContext)
//        setContentView(R.layout.activity_main)
//        setUpToolbar()
//        setUpRecyclerView()
//        setRadioGroupListener()
//        setUpTabs()
//
//    }
//
//    private fun setUpTabs() {
//        mTabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                getTabData(tab.position)
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//                getTabData(tab.position)
//            }
//        })
//    }
//
//    private fun getTabData(tabPosition: Int) {
//        when (tabPosition) {
//            0 -> {
//                mSelectedRole = LEADER
//                getDancers(mSelectedLevel, mSelectedRole)
//            }
//            1 -> {
//                mSelectedRole = FOLLOW
//                getDancers(mSelectedLevel, mSelectedRole)
//            }
//        }
//    }
//
//    private fun setUpRecyclerView() {
//        val linearLayoutManager = LinearLayoutManager(this)
//        mRecyclerView.layoutManager = linearLayoutManager
//
//        mAdapter = DancerAdapter(this, IOnSelected { name ->
//            mSearchName = name
//            mClipData = ClipData.newPlainText("search_name", name)
////            setUpLoginDialog()
//        })
//
//        mRecyclerView.adapter = mAdapter
//    }
//
////    private fun setUpLoginDialog() {
////        mAccessToken = AccessToken.getCurrentAccessToken()
////
////        if (mAccessToken == null || mAccessToken.isExpired()) {
////            val fm = fragmentManager
////            val dialogFragment = LoginDialog()
////            dialogFragment.show(fm, "Login Dialog")
////        } else {
////            FACEBOOK_URL = getFacebookSearchURL(mSearchName)
////            launchFBIntent()
////        }
////    }
//
//    private fun setRadioGroupListener(){
//        mRadioGroup.setOnCheckedChangeListener { group, checkedId ->
//            when (checkedId) {
//                R.id.newcomer_radio_button -> {
//                    mSelectedLevel = NEWCOMER
//                    getDancers(mSelectedLevel, mSelectedRole)
//                }
//                R.id.novice_radio_button -> {
//                    mSelectedLevel = NOVICE
//                    getDancers(mSelectedLevel, mSelectedRole)
//                }
//                R.id.intermediate_radio_button -> {
//                    mSelectedLevel = INTERMEDIATE
//                    getDancers(mSelectedLevel, mSelectedRole)
//                }
//                R.id.advanced_radio_button -> {
//                    mSelectedLevel = ADVANCED
//                    getDancers(mSelectedLevel, mSelectedRole)
//                }
//                R.id.allstar_radio_button -> {
//                    mSelectedLevel = ALL_STAR
//                    getDancers(mSelectedLevel, mSelectedRole)
//                }
//            }
//        }
//    }
//
//    private fun setUpToolbar() {
//        setSupportActionBar(mToolbar)
//        val bar = supportActionBar
//            bar?.setDisplayHomeAsUpEnabled(false)
//            bar?.setDisplayShowTitleEnabled(true)
//            bar?.setTitle(R.string.toolbar_title)
//            bar?.elevation = 2f
//    }
//
//    //TODO: implement cache headers
//    private fun getDancers(division: String, role: String) {
//        val call = APIService.getAPIService().getAllForDivision(role, division)
//        call.enqueue(object : Callback<Dancer> {
//            override fun onResponse(call: Call<Dancer>, response: Response<Dancer>) {
//                if (response.isSuccessful) {
//                    val l = response.body() as List<*>?
//                    mAdapter?.updateAdapter(l)
//                }
//            }
//
//            override fun onFailure(call: Call<Dancer>, t: Throwable) {
//                //                Toast.makeText(MainActivity.this, "Oops, there was a problem, check your network connection", Toast.LENGTH_SHORT).show();
//            }
//        })
//    }
//}