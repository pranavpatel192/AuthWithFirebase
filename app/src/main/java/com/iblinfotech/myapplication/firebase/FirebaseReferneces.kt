package com.iblinfotech.myapplication.firebase

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseReference
import com.iblinfotech.myapplication.firebase.FirebaseReferneces
import com.iblinfotech.myapplication.utils.Const
import java.util.HashMap

object FirebaseReferneces {
    val FIREBASE_DATABASE = FirebaseDatabase.getInstance()
    val DATABASE_REFERENCE = FIREBASE_DATABASE.reference
    fun getDatabaseReference(ref: String?): DatabaseReference {
        return FIREBASE_DATABASE.getReference(ref!!)
    }

    fun createUser(id: String?, firstName: String, email: String, loginType: String) {
        getDatabaseReference(Const.firebaseUsers).child(
            id!!
        ).setValue(id)
        val hashMap = HashMap<String, Any?>()
        hashMap[Const.firebaseUserId] = id
        hashMap[Const.firebaseUserFullName] = firstName
        hashMap[Const.firebaseUserEmail] = email
        hashMap[Const.firebaseUserLoginType] = loginType
        getDatabaseReference(Const.firebaseUsers).child(
            id
        ).updateChildren(hashMap)
    }

    fun updateUserDetails(id: String, firstName: String, email: String, loginType: String) {
        val hashMap = HashMap<String, Any>()
        hashMap[Const.firebaseUserId] = id
        hashMap[Const.firebaseUserFullName] = firstName
        hashMap[Const.firebaseUserEmail] = email
        hashMap[Const.firebaseUserLoginType] = loginType
        getDatabaseReference(Const.firebaseUsers).child(id).updateChildren(hashMap)
    }
}