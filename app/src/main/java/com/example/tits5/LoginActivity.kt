package com.example.tits5

import android.content.Intent
//import android.support.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private var mAuth:FirebaseAuth? = null
    private var database = FirebaseDatabase.getInstance()
    private var myRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mAuth = FirebaseAuth.getInstance()


    }

    fun buLoginEvent(view:View)
    {
        LoginToFirebase(etEmail.text.toString(),etPassword.text.toString())
    }

    fun LoginToFirebase(email:String,password:String) {
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var currentUser = mAuth!!.currentUser
                    Toast.makeText(applicationContext, "Successful Login", Toast.LENGTH_SHORT)
                        .show()

                    if (currentUser != null) {

                        myRef.child("Users")
                            .child(splitString(currentUser.email.toString()))
                            .child("Request").setValue(currentUser.uid)
                    }


                    LoadMain()
                } else {

                    mAuth!!.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                var currentUser = mAuth!!.currentUser
                                Toast.makeText(
                                    applicationContext,
                                    "Successful Login",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (currentUser != null) {

                                    myRef.child("Users")
                                        .child(splitString(currentUser.email.toString()))
                                        .child("Request").setValue(currentUser.uid)
                                    mAuth!!.signInWithEmailAndPassword(email, password)
                                }
                                LoadMain()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Login Failed. Check Email and Password Again!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }

            }
    }

    override fun onStart() {
        super.onStart()
        LoadMain()
    }

    fun LoadMain()
    {
        var currentUser = mAuth!!.currentUser
        if (currentUser != null) {

            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("Email", currentUser.email)
            intent.putExtra("uid", currentUser.uid)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
        }
    }

    fun splitString(str:String):String
    {
        var split = str.split("@")
        return split[0]
    }
}