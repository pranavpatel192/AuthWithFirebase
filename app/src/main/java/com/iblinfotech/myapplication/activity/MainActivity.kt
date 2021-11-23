
package com.iblinfotech.myapplication.activity.activity

import android.content.Intent
import com.iblinfotech.myapplication.R
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.facebook.login.LoginManager
import com.iblinfotech.myapplication.activity.BaseActivity
import com.iblinfotech.myapplication.activity.LoginActivity
import com.iblinfotech.myapplication.databinding.ActivityMainBinding
import com.iblinfotech.myapplication.utils.Const

class MainActivity : BaseActivity() {

    // BINDING
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // LOGOUT BUTTON CLICK
        initLogoutButtonClick()
    }

    // LOGOUT BUTTON CLICK
    private fun initLogoutButtonClick(){
        binding.btnLogout.setOnClickListener {
            if (sessionManager.loginType == Const.facebookLogin){
                LoginManager.getInstance().logOut()
            }
            sessionManager.clearData()
            val intent = Intent(context, LoginActivity :: class.java)
            startActivity(intent)
        }
    }
}