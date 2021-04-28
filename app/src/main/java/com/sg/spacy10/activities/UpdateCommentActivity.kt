package com.sg.spacy10.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.spacy10.R
import com.sg.spacy10.databinding.ActivityUpdateCommentBinding
import com.sg.spacy10.utilities.*

class UpdateCommentActivity : AppCompatActivity() {
    lateinit var binding:ActivityUpdateCommentBinding
    lateinit var thoughtDocId:String
    lateinit var commentDocId:String
    lateinit var commentTxt:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpdateCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
 thoughtDocId=intent.getStringExtra(THOUGHT_DOC_ID_EXTRA)
        commentDocId=intent.getStringExtra(COMMENT_DOC_ID_EXTRA)
        commentTxt=intent.getStringExtra(COMMENT_TXT_EXTRA)

        binding.updateCommentText.setText(commentTxt)
    }

    fun updateCommentClicked(view: View) {
        FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocId)
            .collection(COMMENTS_REF).document(commentDocId)
            .update(COMMENTS_TXT,binding.updateCommentText.text.toString())
            .addOnSuccessListener {
                hideKeyboard()
                finish()
            }.addOnFailureListener {
                Log.e(TAG,"cannot update commant because :${it.localizedMessage}")
            }
    }
    private fun hideKeyboard(){
        val inputManagar=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManagar.isAcceptingText){
            inputManagar.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }

}