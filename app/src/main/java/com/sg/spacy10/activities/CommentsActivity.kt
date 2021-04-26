package com.sg.spacy10.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sg.spacy10.R
import com.sg.spacy10.adaptersThoghtaAdapter.kt.CommentsAdapter
import com.sg.spacy10.databinding.ActivityCommentsBinding
import com.sg.spacy10.model.Comment
import com.sg.spacy10.utilities.*


class CommentsActivity : AppCompatActivity() {
    lateinit var binding:ActivityCommentsBinding
    lateinit var thoughtDocumentId:String
    val comments = arrayListOf<Comment>()
    lateinit var commentsAdapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityCommentsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        thoughtDocumentId=intent.getStringExtra(DOCUMENT_KEY)

        commentsAdapter= CommentsAdapter(comments)
        binding.commentsListview.adapter=commentsAdapter
        val layoutManager= LinearLayoutManager(this)
        binding.commentsListview.layoutManager=layoutManager

        FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)
            .collection(COMMENTS_REF)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception!=null){
                    Log.i(com.sg.spacy10.utilities.TAG,"Cannt retrive comments because :${exception.localizedMessage}")
                }
                if (snapshot!=null){
                    comments.clear()
                    for (document in snapshot.documents){
                        val data=document.data
                        val name= data?.get(USERNAME) as String
                        val timestamp=data[TIMESTAMP] as Timestamp
                        val commentText=data[COMMENTS_TXT] as String
                        val documentId=document.id
                        val userId=data[USER_ID] as String
                        val newComment= Comment(name,timestamp,commentText,documentId,userId)
                        comments.add(newComment)
                    }
                    commentsAdapter.notifyDataSetChanged()
                }
            }

    }

    fun addCommentClick(view: View) {
        val commentText = binding.enterCommentText.text.toString()

        val thoughtRef =
            FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)

        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val thought = transaction.get(thoughtRef)
            val numComments = thought.getLong(NUM_COMMENTS)?.plus(1)
            transaction.update(thoughtRef, NUM_COMMENTS, numComments)

            val newCommentRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                .document(thoughtDocumentId).collection(COMMENTS_REF).document()
            val data = HashMap<String, Any>()
            data.put(COMMENTS_TXT, commentText)
            data.put(TIMESTAMP, FieldValue.serverTimestamp())
            data.put(USERNAME, FirebaseAuth.getInstance().currentUser.displayName.toString())
            data.put(USER_ID, FirebaseAuth.getInstance().currentUser?.uid.toString())
            transaction.set(newCommentRef, data)
        }.addOnSuccessListener {
            binding.enterCommentText.setText("")
            hideKeyboard()
        }
            .addOnFailureListener { exception ->
                Log.i(TAG, "could not add comment:${exception.localizedMessage}")
            }
    }
    private fun hideKeyboard(){
        val inputManagar=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManagar.isAcceptingText){
            inputManagar.hideSoftInputFromWindow(currentFocus?.windowToken,0)
        }
    }
}