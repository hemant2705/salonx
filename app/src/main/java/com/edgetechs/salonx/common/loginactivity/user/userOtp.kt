package com.edgetechs.salonx.common.loginactivity.user

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.chaos.view.PinView
import com.edgetechs.salonx.R
import com.edgetechs.salonx.common.loginactivity.merchant.DataHelper
import com.edgetechs.salonx.common.loginactivity.merchant.confirmation2
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class userOtp : AppCompatActivity() {
    lateinit var pinFromUser: PinView
    lateinit var Login: Button
    lateinit var phone: String
    lateinit var codeBySystem:String

    lateinit var str1: String
    lateinit var str2: String
    lateinit var str3: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_otp)


        pinFromUser = findViewById(R.id.pinView)
        Login = findViewById(R.id.submit)


        val sharedPreferences = getSharedPreferences("userKey", Context.MODE_PRIVATE)

        str1 = sharedPreferences.getString("fullname", "0").toString();
        str2 = sharedPreferences.getString("mobile", "0").toString()
        str3 = sharedPreferences.getString("pass", "0").toString()

        phone = "+91" + sharedPreferences.getString("mobile", "0").toString()
        sendVerificationCodeToUser(phone)
        Login.setOnClickListener {
            val io = pinFromUser.text.toString()

            verifyCode(io)

        }
    }

    private fun sendVerificationCodeToUser(phoneNo: String) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNo,  // Phone number to verify
            60,  // Timeout duration
            TimeUnit.SECONDS,  // Unit of timeout
            this,  // Activity (for callback binding)
            mCallbacks
        ) // OnVerificationStateChangedCallbacks
    }
    private val mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks =
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(
                s: String,
                forceResendingToken: PhoneAuthProvider.ForceResendingToken
            ) {
                super.onCodeSent(s, forceResendingToken)
                codeBySystem = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                Toast.makeText(this@userOtp, "Otp sent succesfully", Toast.LENGTH_SHORT).show()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@userOtp, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(codeBySystem, code)
        signInWithPhoneAuthCredential(credential)
    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    //Verification completed successfully here Either
                    // store the data or do whatever desire
                    new()
                    val intent = Intent(this, userLogin::class.java)
                    startActivity(intent)
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                        Toast.makeText(
                            this@userOtp,
                            "Verification Not Completed! Try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
    }


    fun new() {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("User")
        val addnew =
            userDatahelper(
                str1,
                str2,
                str3)
        myRef.child(str2).setValue(addnew)
    }
}

