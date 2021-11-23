package com.iblinfotech.myapplication.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dev.practical.extra.Utils
import com.dev.practical.extra.ValidationInputs
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.iblinfotech.myapplication.R
import com.iblinfotech.myapplication.databinding.ActivitySignUpBinding
import com.iblinfotech.myapplication.firebase.FirebaseReferneces
import com.iblinfotech.myapplication.utils.Const

class SignUpActivity : BaseActivity() {

    // BINDING
    lateinit var binding: ActivitySignUpBinding

    // FIREBASE AUTH
    private lateinit var mFirebaseAuth: FirebaseAuth

    // ARRAY LIST
    var emailList : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // INIT FIREBASE AUTH
        mFirebaseAuth = FirebaseAuth.getInstance()

        // INIT REGISTER BUTTON CLICK
        initRegisterButtonClick()

    }

    // INIT REGISTER BUTTON CLICK
    private fun initRegisterButtonClick(){
        binding.btnSignUp.setOnClickListener {
            checkValidation()
        }
    }

    // CHECK VALIDATION
    private fun checkValidation(){
        if (binding.edFullName.text.toString().trim().isEmpty()){
            Utils.showAlertCustom(context, context.resources.getString(R.string.error_empty_full_name))
        } else if (binding.edEmail.text.toString().trim().isEmpty()){
            Utils.showAlertCustom(context, context.resources.getString(R.string.error_empty_email))
        } else if (!ValidationInputs.isValidEmail(binding.edEmail.text.toString().trim())){
            Utils.showAlertCustom(context, context.resources.getString(R.string.error_valid_email))
        } else if (binding.edPassword.text.toString().trim().isEmpty()){
            Utils.showAlertCustom(context, context.resources.getString(R.string.error_empty_password))
        } else {
            isUsersExists()
        }
    }


    // CHECK FIREBASE DATA AVIALABLE
    private fun isUsersExists(){
        FirebaseReferneces.getDatabaseReference(Const.firebaseUsers).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.value is ArrayList<*>) {
                        val objectMap: ArrayList<Map<String, Any>?> = snapshot.value as ArrayList<Map<String, Any>?>
                        var userId : String = ""
                        for (i in 0 until objectMap.size) {
                            if (objectMap[i] != null) {
                                userId = objectMap[i]!!.getValue(Const.firebaseUserId) as String
                                if (objectMap[i]!!.containsKey(Const.firebaseUserEmail)){
                                    val email = objectMap[i]!!.getValue(Const.firebaseUserEmail) as String
                                    emailList.add(email)
                                }
                            }
                        }
                        if (emailList.size > 0){
                            if (emailList.contains(binding.edEmail.text.toString().trim())){
                                Utils.showAlertCustom(context, context.resources.getString(R.string.email_exists))
                            } else {
                                sessionManager.userId = userId.toInt() + 1
                                sessionManager.name = binding.edFullName.text.toString().trim()
                                sessionManager.email = binding.edEmail.text.toString().trim()
                                sessionManager.password = binding.edPassword.text.toString().trim()
                                setFirebaseLogin(binding.edEmail.text.toString().trim(), binding.edPassword.text.toString().trim())
                            }
                        }
                    } else {
                        sessionManager.userId = 0
                        sessionManager.name = binding.edFullName.text.toString().trim()
                        sessionManager.email = binding.edEmail.text.toString().trim()
                        sessionManager.password = binding.edPassword.text.toString().trim()
                        setFirebaseLogin(binding.edEmail.text.toString().trim(), binding.edPassword.text.toString().trim())

                    }
                } catch (e : Exception){
                    e.printStackTrace()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    "Database Error->",
                    error.details
                )
            }

        })
    }


    // FIREBASE LOGIN
    private fun setFirebaseLogin(
        email : String,
        password : String
    ) {
        dialogLoader.showProgressDialog()

        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, OnCompleteListener {
            if (!it.isSuccessful) {
                Log.e("Firebase Login", "failed")
                setFirebaseRegistration(email, password)
            } else {
                Log.e("Firebase Login", "success")

                FirebaseReferneces.getDatabaseReference(Const.firebaseUsers).child(sessionManager.userId.toString()).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val objectMap: Map<String, Any>? =
                            dataSnapshot.value as HashMap<String, Any>?
                        if (objectMap != null) {
                            val id = objectMap[Const.firebaseUserId].toString()
                            if (id.contains(java.lang.String.valueOf(sessionManager.userId))) {
                                FirebaseReferneces.updateUserDetails(
                                    sessionManager.userId.toString(),
                                    sessionManager.name,
                                    sessionManager.email)
                            }
                        } else {
                            FirebaseReferneces.createUser(
                                sessionManager.userId.toString(),
                                sessionManager.name,
                                sessionManager.email)
                        }
                        dialogLoader.hideProgressDialog()
                        finish()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e(
                            "Database Error->",
                            databaseError.details
                        )
                    }

                })

            }
        })

    }


    // FIREBASE REGISTRATION
    private fun setFirebaseRegistration(
        email : String,
        password : String

    ) {
        mFirebaseAuth!!.createUserWithEmailAndPassword(
            email, password
        )
            .addOnFailureListener { e -> e.printStackTrace() }
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    setFirebaseLogin(email, password)
                } else {
                    Log.e("ERROR", "")
                }
            }
    }



}