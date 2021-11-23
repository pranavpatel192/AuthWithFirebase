package com.dev.practical.extra

import android.content.Context
import android.content.SharedPreferences

class SessionManager(
    //Context
    context: Context
) {

    // Shared Preferences
    var pref: SharedPreferences

    // Editor for Shared preferences
    var editor: SharedPreferences.Editor

    // Shared pref mode
    var PRIVATE_MODE = 0

    fun clearData() { // Clearing all data from Shared Preferences
        editor.clear()
        editor.commit()
    }


    var name : String?
        get() = pref.getString(NAME, "")
        set(name) {
            editor.putString(NAME, name)
            editor.commit()
        }



    var email : String?
        get() = pref.getString(EMAIL, "")
        set(email) {
            editor.putString(EMAIL, email)
            editor.commit()
        }

    var password : String?
        get() = pref.getString(PASSWORD, "")
        set(email) {
            editor.putString(PASSWORD, email)
            editor.commit()
        }

    var mobileNumber : String?
        get() = pref.getString(MOBILE_NUMBER, "")
        set(mobileNumber) {
            editor.putString(MOBILE_NUMBER, mobileNumber)
            editor.commit()
        }

    var profilePic : String?
        get() = pref.getString(PROFILE, "")
        set(profile) {
            editor.putString(PROFILE, profile)
            editor.commit()
        }


    var isUserLoggedIn : Boolean?
        get() = pref.getBoolean(IS_USER_LOGGED_IN, false)
        set(value) {
            editor.putBoolean(IS_USER_LOGGED_IN, value!!)
            editor.commit()
        }

    var userId : Int?
        get() = pref.getInt(USER_ID, 0)
        set(id) {
            editor.putInt(USER_ID, id!!)
            editor.commit()
        }


    companion object {
        private var sessionManager: SessionManager? = null

        // Sharedpref file name
        private const val PREF_NAME = "taskPref"

        // All Shared Preferences Keys
        private const val NAME = "name"
        private const val EMAIL = "email"
        private const val MOBILE_NUMBER = "mobile_number"
        private const val IS_USER_LOGGED_IN = "is_user_logged_in"
        private const val USER_ID = "user_id"
        private const val PASSWORD = "password"
        private const val PROFILE = "profile"

        @Synchronized
        fun getInstance(context: Context): SessionManager? {
            if (sessionManager == null) {
                sessionManager = SessionManager(context.applicationContext)
            }
            return sessionManager
        }
    }

    // Constructor
    init {
        pref = context.getSharedPreferences(
            PREF_NAME,
            PRIVATE_MODE
        )
        editor = pref.edit()
    }
}