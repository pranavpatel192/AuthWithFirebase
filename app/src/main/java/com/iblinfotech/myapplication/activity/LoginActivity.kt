package com.iblinfotech.myapplication.activity

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.dev.practical.extra.Utils
import com.dev.practical.extra.ValidationInputs
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.iblinfotech.myapplication.R
import com.iblinfotech.myapplication.activity.activity.MainActivity
import com.iblinfotech.myapplication.databinding.ActivityLoginBinding
import com.iblinfotech.myapplication.firebase.FirebaseReferneces
import com.iblinfotech.myapplication.utils.Const
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class LoginActivity : BaseActivity() {

    // BINDING
    lateinit var binding: ActivityLoginBinding

    // FIREBASE AUTH
    private lateinit var mFirebaseAuth: FirebaseAuth

    // CALL BACK MANAGER
    lateinit var callBackManager : CallbackManager

    // ARRAY LIST
    var emailList : ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        // INIT FIREBASE AUTH
        mFirebaseAuth = FirebaseAuth.getInstance()

        // FACEBOOK KEY HASH
        facebookHashKey()

        // INIT SIGN IN CLICK
        initLoginClick()

        // INIT SIGN UP CLICK
        initSignUpClick()

        // INIT FACEBOOK LOGIN CLICK
        initFacebookLoginClick()
    }

    // INIT SIGN IN CLICK
    private fun initLoginClick(){
        binding.btnSignIn.setOnClickListener {
            checkValidation()
        }
    }

    // INIT SIGN UP CLICK
    private fun initSignUpClick(){
        binding.txSignUp.setOnClickListener {
            val intent = Intent(context, SignUpActivity :: class.java)
            startActivity(intent)
        }
    }

    // INIT FACEBOOK LOGIN CLICK
    private fun initFacebookLoginClick(){
        // Initialize Facebook Login button
        callBackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().logOut()

        binding.fbLogin.setReadPermissions("email", "public_profile")
        binding.fbLogin.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("TAG", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d("TAG", "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d("TAG", "facebook:onError", error)
            }
        })
    }


    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d("TAG", "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        mFirebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = mFirebaseAuth.currentUser
                    val name = user!!.displayName.toString()
                    val email = user!!.email.toString()
                    dialogLoader.showProgressDialog()
                    isUsersExists(name, email)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


    // CHECK FIREBASE DATA AVAILABLE
    private fun isUsersExists(name : String, email : String){
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
                            if (emailList.contains(email)){
                                val main = Intent(context, MainActivity::class.java)
                                main.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(main)
                            } else {
                                sessionManager.userId = userId.toInt() + 1
                                sessionManager.name = name
                                sessionManager.email = email
                                sessionManager.password = ""
                                FirebaseReferneces.updateUserDetails(
                                    sessionManager.userId.toString(),
                                    sessionManager.name.toString(),
                                    sessionManager.email.toString(),
                                Const.facebookLogin)
                                dialogLoader.hideProgressDialog()
                                val main = Intent(context, MainActivity::class.java)
                                main.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(main)
                            }

                        }
                    } else {
                        sessionManager.userId = 0
                        sessionManager.name = name
                        sessionManager.email = email
                        sessionManager.password = ""
                        FirebaseReferneces.updateUserDetails(
                            sessionManager.userId.toString(),
                            sessionManager.name.toString(),
                            sessionManager.email.toString(),
                        Const.facebookLogin)
                        dialogLoader.hideProgressDialog()
                        val main = Intent(context, MainActivity::class.java)
                        main.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(main)
                    }
                } catch (e : Exception){
                    e.printStackTrace()
                }

            }

            override fun onCancelled(error: DatabaseError) {
                dialogLoader.hideProgressDialog()
                Log.e(
                    "Database Error->",
                    error.details
                )
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callBackManager.onActivityResult(requestCode, resultCode, data)
    }


    // CHECK VALIDATION
    private fun checkValidation(){
        if (binding.edEmail.text.toString().trim().isEmpty()){
            Utils.showAlertCustom(context, context.resources.getString(R.string.error_empty_email))
        } else if (!ValidationInputs.isValidEmail(binding.edEmail.text.toString().trim())){
            Utils.showAlertCustom(context, context.resources.getString(R.string.error_valid_email))
        } else if (binding.edPassword.text.toString().trim().isEmpty()){
            Utils.showAlertCustom(
                context,
                context.resources.getString(R.string.error_empty_password)
            )
        } else {
            setFirebaseLogin(
                binding.edEmail.text.toString().trim(),
                binding.edPassword.text.toString().trim()
            )
        }
    }

    // SET FIREBASE LOGIN
    private fun setFirebaseLogin(email: String, password: String){
        dialogLoader.showProgressDialog()
        dialogLoader.showProgressDialog()

        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
            this,
            OnCompleteListener {
                if (!it.isSuccessful) {
                    Log.e("Firebase Login", "failed")
                } else {
                    Log.e("Firebase Login", "success")

                    FirebaseReferneces.getDatabaseReference(Const.firebaseUsers)
                        .child(sessionManager.userId.toString()).addListenerForSingleValueEvent(
                        object :
                            ValueEventListener {
                            override fun onDataChange(dataSnapshot: DataSnapshot) {
                                dialogLoader.hideProgressDialog()
                                val objectMap: Map<String, Any>? =
                                    dataSnapshot.value as HashMap<String, Any>?
                                if (objectMap != null) {
                                    val id = objectMap[Const.firebaseUserId].toString()
                                    sessionManager.name =
                                        objectMap[Const.firebaseUserFullName].toString()
                                    sessionManager.email =
                                        objectMap[Const.firebaseUserEmail].toString()
                                    sessionManager.isUserLoggedIn = true
                                    if (id.contains(java.lang.String.valueOf(sessionManager.userId))) {
                                        val main = Intent(context, MainActivity::class.java)
                                        main.flags =
                                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        context.startActivity(main)
                                    }
                                }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                dialogLoader.hideProgressDialog()
                                Log.e(
                                    "Database Error->",
                                    databaseError.details
                                )
                            }

                        })

                }
            })

    }

    // FACE BOOK KEY HASH
    private fun facebookHashKey(){
        val info: PackageInfo
        try {
            info = packageManager.getPackageInfo("com.you.name", PackageManager.GET_SIGNATURES)
            for (signature in info.signatures) {
                var md: MessageDigest
                md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val something: String = String(Base64.encode(md.digest(), 0))
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("hash key", something)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            Log.e("name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            Log.e("no such an algorithm", e.toString())
        } catch (e: Exception) {
            Log.e("exception", e.toString())
        }
    }
}