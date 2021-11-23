
package com.iblinfotech.myapplication.activity.activity

import com.iblinfotech.myapplication.R
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.iblinfotech.myapplication.activity.BaseActivity
import com.iblinfotech.myapplication.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {

    // BINDING
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
    }
}