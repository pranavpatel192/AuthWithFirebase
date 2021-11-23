package com.iblinfotech.myapplication.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.iblinfotech.myapplication.utils.Const;
import java.util.HashMap;

public class FirebaseReferneces {


    public static final FirebaseDatabase FIREBASE_DATABASE = FirebaseDatabase.getInstance();
    public static final DatabaseReference DATABASE_REFERENCE = FIREBASE_DATABASE.getReference();

    public static DatabaseReference getDatabaseReference(String ref) {
        return FIREBASE_DATABASE.getReference(ref);
    }

    public static void createUser(String id, String firstName, String email) {
        getDatabaseReference(Const.firebaseUsers).child(id).setValue(id);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Const.firebaseUserId, id);
        hashMap.put(Const.firebaseUserFullName, firstName);
        hashMap.put(Const.firebaseUserEmail, email);
        getDatabaseReference(Const.firebaseUsers).child(id).updateChildren(hashMap);
    }

    public static void updateUserDetails(String id, String firstName,String email) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Const.firebaseUserId, id);
        hashMap.put(Const.firebaseUserFullName, firstName);
        hashMap.put(Const.firebaseUserEmail, email);
        getDatabaseReference(Const.firebaseUsers).child(id).updateChildren(hashMap);
    }



}
