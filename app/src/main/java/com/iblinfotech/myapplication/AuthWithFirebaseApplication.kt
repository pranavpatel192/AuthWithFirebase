package com.iblinfotech.myapplication

import android.app.Application
import com.google.firebase.FirebaseApp

class AuthWithFirebaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)
    }


    companion object{
        var instance: Application? = null
            private set

        val TAG: String = AuthWithFirebaseApplication ::class.java.simpleName


    }
}