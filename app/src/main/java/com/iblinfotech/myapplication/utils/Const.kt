package com.iblinfotech.myapplication.utils

interface Const {
    companion object{

        // FOR USERS TABLE
        const val firebaseUsers : String = "users"
        const val firebaseUserId : String = "user_id"
        const val firebaseUserFullName : String = "full_name"
        const val firebaseUserProfilePic : String = "profile_pic"
        const val firebaseUserEmail : String = "email"
        const val firebaseUserPassword : String = "password"
        const val firebaseUserLoginType : String = "login_type"
        const val normalLogin : String = "normal_login"
        const val facebookLogin : String = "facebook_login"

    }
}