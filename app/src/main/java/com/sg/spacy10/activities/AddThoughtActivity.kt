package com.sg.spacy10.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.spacy10.R
import com.sg.spacy10.databinding.ActivityAddThoughtBinding
import com.sg.spacy10.utilities.*

class AddThoughtActivity : AppCompatActivity() {
    var selectedCategory= FUNNY
    lateinit var binding:ActivityAddThoughtBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityAddThoughtBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    fun post_Onclicked(view: View) {
        val data=HashMap<String,Any>()
        data.put(CATEGORY,selectedCategory)
        data.put(NUM_COMMENTS,0)
        data.put(NUM_LIKES,0)
        data.put(THOUGHT_TXT,binding.addThoughtText.text.toString())
        data.put(USERNAME,FirebaseAuth.getInstance().currentUser.displayName.toString())
        data.put(TIMESTAMP,FieldValue.serverTimestamp())
        FirebaseFirestore.getInstance().collection(THOUGHTS_REF).add(data)
            .addOnSuccessListener {
                finish()
            }.addOnFailureListener { e->
                Log.e(TAG,"Can't connect to fire base because: ${e.localizedMessage}")
            }
    }

    fun addFunnyOnclicked(view: View) {
        binding.addFunnyBtn.isChecked=true
        binding.addSeriousBtn.isChecked=false
        binding.addCrazyBtn.isChecked=false
        selectedCategory= FUNNY
    }
    fun addSeriousOnClick(view: View) {
        binding.addFunnyBtn.isChecked=false
        binding.addSeriousBtn.isChecked=true
        binding.addCrazyBtn.isChecked=false
        selectedCategory= SERIOUS
    }

    fun addCrazyOnClicked(view: View) {
        binding.addFunnyBtn.isChecked=false
        binding.addSeriousBtn.isChecked=false
        binding.addCrazyBtn.isChecked=true
        selectedCategory= CRAZY
    }
}