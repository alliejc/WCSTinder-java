package com.alliejc.wcstinder.fragment

import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.alliejc.wcstinder.R
import com.alliejc.wcstinder.callback.ICallback
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.dialog_login.*
import java.util.*

class LoginDialog:DialogFragment() {
    val EMAIL = "email"
    val TAG = LoginDialog().tag

    var mCallbackManager: CallbackManager? = null
    var mCallback: ICallback<AccessToken, String>? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        var root = inflater!!.inflate(R.layout.dialog_login, container, false)
        login_button.setFragment(this)
        login_button.setReadPermissions(Arrays.asList(EMAIL))

        mCallbackManager = CallbackManager.Factory.create()
        login_button.registerCallback(mCallbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult?) {
                mCallback?.onCompleted(result!!.accessToken as AccessToken)
            }

            override fun onError(error: FacebookException?) {
                login_text.text = "Login attempt failed"
                mCallback?.onError(error?.message)
            }

            override fun onCancel() {
                login_text.text = "Login attempt cancelled"
            }
        })
        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mCallback = activity as ICallback<AccessToken, String>
        } catch (e: Exception) {
            Log.d(TAG, e.message)
        }

    }
}