package com.sg.spacy10.adaptersThoghtaAdapter.kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.sg.spacy10.R
import com.sg.spacy10.interfaces.CommentOptionClickListener
import com.sg.spacy10.model.Comment

class CommentsAdapter(
    private val comments: ArrayList<Comment>,
    val commentOptionListener: CommentOptionClickListener
) :
    RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent?.context).inflate(R.layout.comment_list_view, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindComment(comments[position])
    }

    override fun getItemCount() = comments.count()


    inner class ViewHolder(itemView: View?) :
        RecyclerView.ViewHolder(itemView!!) {
        val username = itemView?.findViewById<TextView>(R.id.commentListUserName)
        val timestap = itemView?.findViewById<TextView>(R.id.commentListTimestap)
        val commentTxt = itemView?.findViewById<TextView>(R.id.commentListCommentText)
        val optionImage = itemView?.findViewById<ImageView>(R.id.commentOptionImage)


        fun bindComment(comment: Comment) {
            optionImage?.visibility = View.INVISIBLE
            username?.text = comment.username
            commentTxt?.text = comment.commentTxt
            timestap?.text = comment.timestamp?.toDate().toString()
            if (FirebaseAuth.getInstance().currentUser?.uid == comment.userId) {
                optionImage?.visibility = View.VISIBLE
                optionImage?.setOnClickListener {
                    commentOptionListener.OptionMenuClicked(comment)
                }
            }
        }
    }
}