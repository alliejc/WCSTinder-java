package com.alliejc.wcstinder.activity

import android.app.FragmentManager
import android.app.FragmentTransaction
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.alliejc.wcstinder.DancerComparator
import com.alliejc.wcstinder.R
import com.alliejc.wcstinder.adapter.DancerAdapter
import com.alliejc.wcstinder.callback.IOnSelected
import com.alliejc.wcstinder.ext.inTransaction
import com.alliejc.wcstinder.fragment.LoginDialog
import com.alliejc.wcstinder.trackmyswing.APIService
import com.alliejc.wcstinder.trackmyswing.Dancer
import com.alliejc.wcstinder.util.Constants.*
import com.facebook.AccessToken
import com.facebook.AccessTokenTracker
import com.facebook.FacebookSdk
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val TAG = MainActivity::class.java.simpleName

    private var mClipboard: ClipboardManager? = null
    private var mClipData: ClipData? = null
    private var mAccessTokenTracker: AccessTokenTracker? = null
    private var mAccessToken: AccessToken? = null
    var mDancers: MutableList<Dancer>? = mutableListOf()
    private var mSelectedLevel = NEWCOMER
    private var mSelectedRole = LEADER
    lateinit var mAdapter: DancerAdapter
    private var mSearchName = ""
    lateinit var mToolbar: Toolbar
    lateinit var mRelevanceFilter: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_main)

        mAccessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(oldAccessToken: AccessToken, currentAccessToken: AccessToken) {
                mAccessToken = currentAccessToken
            }
        }

        mAccessTokenTracker?.startTracking()
        mClipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

        setUpToolbar()
        setClickListeners()
        setUpRecyclerView()
        level_radio_group.check(R.id.newcomer_radio_button)
        getDancers(role = mSelectedRole, division =  mSelectedLevel)
        mAdapter.updateAdapter(mDancers)
        setRelevanceFilter()
    }

    private fun setUpLoginDialog() {
        mAccessToken = AccessToken.getCurrentAccessToken()

        if (mAccessToken != null) {
            fragmentManager.inTransaction {
                add(LoginDialog(), "Login Dialog")
            }
        } else {
            FACEBOOK_URL = getFacebookSearchURL(mSearchName)
            launchFBIntent()
        }
    }

    private fun setUpRecyclerView() {
        val linearLayout = LinearLayoutManager(this)
        recycler_view.layoutManager = linearLayout

        mAdapter = DancerAdapter(this, IOnSelected { name ->
            mSearchName = name
            mClipData = ClipData.newPlainText("search_name", name)
            setUpLoginDialog()
        })
        recycler_view.adapter = mAdapter
    }

    private fun setClickListeners() {
        select_level_header.setOnClickListener {
            toggleExpandableLayout()
        }

        level_radio_group.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.newcomer_radio_button ->
                    mSelectedLevel = NEWCOMER

                R.id.novice_radio_button ->
                    mSelectedLevel = NOVICE

                R.id.intermediate_radio_button ->
                    mSelectedLevel = INTERMEDIATE

                R.id.advanced_radio_button ->
                    mSelectedLevel = ADVANCED

                R.id.allstar_radio_button ->
                    mSelectedLevel = ALL_STAR
            }

            toggleExpandableLayout()
            getDancers(role = mSelectedRole, division =  mSelectedLevel)
            mAdapter.updateAdapter(mDancers)
        }

        role_tablayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                getTabData(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}

            override fun onTabReselected(tab: TabLayout.Tab) {
                getTabData(tab.position)
            }
        })
    }

    private fun getTabData(tabPosition: Int) {
        when (tabPosition) {
            0 -> mSelectedRole = LEADER
            1 -> mSelectedRole = FOLLOW
        }

        getDancers(role = mSelectedRole, division =  mSelectedLevel)
        mAdapter.updateAdapter(mDancers)
    }

    private fun toggleExpandableLayout() {
        if (expandable_layout.isExpanded) {
            expandable_layout.collapse()
            expand_collapse_icon.setImageDrawable(resources.getDrawable(R.drawable.ic_expand_more))
        } else {
            expandable_layout.expand()
            expand_collapse_icon.setImageDrawable(resources.getDrawable(R.drawable.ic_expand_less))
        }
    }

    private fun setUpToolbar() {
        mToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(mToolbar)
        val bar = supportActionBar
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(false)
            bar.setDisplayShowTitleEnabled(true)
            bar.setTitle(R.string.toolbar_title)
            bar.elevation = 2f
        }
    }

    private fun launchFBIntent() {
        if (FACEBOOK_URL.equals("fb://search/")) {
            mClipboard?.primaryClip = mClipData
            Toast.makeText(applicationContext, mSearchName + " " + "saved to clipboard", Toast.LENGTH_LONG).show()
        }

        FACEBOOK_URL = getFacebookSearchURL(mSearchName)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(FACEBOOK_URL)
        startActivity(intent)
    }

    private fun getFacebookSearchURL(searchName: String): String? {
        try {
            var versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode
            return "fb://search/"
        } catch (e: PackageManager.NameNotFoundException) {
            return "https://www.facebook.com/search/people/?q=" + searchName //normal web url
        }
    }

    private fun getDancers(division: String, role: String) {
        var call = APIService.getAPIService().getAllForDivision(role, division)
        call.enqueue(object : Callback<MutableList<Dancer>> {
            override fun onFailure(call: Call<MutableList<Dancer>>, t: Throwable?) {
                Log.e(TAG, t?.message)
            }

            override fun onResponse(call: Call<MutableList<Dancer>>, response: Response<MutableList<Dancer>>) {
                if (response.isSuccessful) {
                    Log.e(TAG, response.message())
                    var list = sortByRelevance(response.body())
                    mDancers = list as MutableList<Dancer>
                }
            }
        })
    }

    fun setRelevanceFilter() {
        mRelevanceFilter = findViewById(R.id.filter_spinner)
        var arrayAdapter = ArrayAdapter.createFromResource(this, R.array.relevance_filter, android.R.layout.simple_spinner_item)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        mRelevanceFilter.adapter = arrayAdapter
        mRelevanceFilter.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    fun getDancersForRelevance(relevance: Int): MutableList<Dancer>{
        getDancers(role = mSelectedRole, division =  mSelectedLevel)
        var list: MutableList<Dancer> = mutableListOf()
        for(dancer in mDancers!!){
            if(dancer.relevance == relevance){
                list.add(dancer)
            }
        }
        return list
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (mDancers != null) {
            when (position) {
                0 -> {
                    mAdapter.updateAdapter(getDancersForRelevance(0))
                }
                1 -> {
                    mAdapter.updateAdapter(getDancersForRelevance(1))
                }
                2 -> {
                    mAdapter.updateAdapter(getDancersForRelevance(2))
                }
                3 -> {
                    mAdapter.updateAdapter(getDancersForRelevance(3))
                }
                4 -> {
                    mAdapter.updateAdapter(getDancersForRelevance(4))
                }
                5 -> {
                    mAdapter.updateAdapter(getDancersForRelevance(5))
                }
            }
        }
    }

    private fun sortByRelevance(list: MutableList<Dancer>?): MutableList<Dancer>? {
        Collections.sort(list, DancerComparator())
        return list
    }
}

