package com.iblinfotech.myapplication.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dev.practical.extra.DialogLoader
import com.dev.practical.extra.SessionManager

open class BaseActivity : AppCompatActivity() {

    //CONTEXT
    lateinit var context: Context

    // SESSION MANAGER
    open lateinit var sessionManager: SessionManager

    // DIALOG LOADER
    open lateinit var dialogLoader: DialogLoader

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // INIT CONTEXT
        context = this

        // INIT SESSION MANAGER
        sessionManager = SessionManager.getInstance(context)!!

        // INIT DIALOG LOADER
        dialogLoader = DialogLoader(context)
    }


}