package com.sg.spacy10.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.sg.spacy10.utilities.TAG
import com.sg.spacy10.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth= FirebaseAuth.getInstance()
    }


    fun loginLoginClicked(view: View) {
        /*val email=loginEmailTxt.text.toString()
        val password=loginPasswordTxt.text.toString()*/
        /*val email="shlomo10@gmail.com"
        val password="123456"*/
        /*val email="shlomo11@gmail.com"
        val password="123456"*/
        val username=binding.loginEmailTxt.text.toString()
        val email="${username}@gmail.com"
        var password=binding.loginPasswordTxt.text.toString()
        password="${password}11111"



        Log.i(TAG,"inside loginActivity1 email=$email,password=$password")
        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                Log.i(TAG,"inside loginActivity2 email=$email,password=$password")
                finish()
            }
            .addOnFailureListener { exception->
                Log.e(TAG,"Could not sign in usert:${exception.localizedMessage}")
            }
    }

    fun loginCreateUserClicked(view: View) {
        val intent = Intent(this, CreateUserActivity::class.java)
        startActivity(intent)
    }

}