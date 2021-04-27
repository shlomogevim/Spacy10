package com.sg.jhony50

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sg.spacy10.R
import com.sg.spacy10.interfaces.ThoughtOptionClickListener
import com.sg.spacy10.model.Thought
import com.sg.spacy10.utilities.NUM_LIKES
import com.sg.spacy10.utilities.THOUGHTS_REF


class ThoughtsAdapter(
    private val thoughts: ArrayList<Thought>,
    val thoughtOptionListener:ThoughtOptionClickListener,
    private val itemClick: (Thought) -> Unit
) :
    RecyclerView.Adapter<ThoughtsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.thought_list_view, parent, false)
        return ViewHolder(view, itemClick)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindThought(thoughts[position])
    }

    override fun getItemCount() = thoughts.count()


    inner class ViewHolder(itemView: View?, val itemClick: (Thought) -> Unit) :
        RecyclerView.ViewHolder(itemView!!) {
        private val username = itemView?.findViewById<TextView>(R.id.listViewUsername)
        private val timestamp = itemView?.findViewById<TextView>(R.id.listViewTimestamp)
        private val thoughtsText = itemView?.findViewById<TextView>(R.id.listViewToughtTxt)
        private val numLikes = itemView?.findViewById<TextView>(R.id.listViewNumLikes)
        private val likesImage = itemView?.findViewById<ImageView>(R.id.listViewLikesImage)
        private val numComments = itemView?.findViewById<TextView>(R.id.numCommentsLabel)
        private val optionImage = itemView?.findViewById<ImageView>(R.id.thoughtOptionImage)


        fun bindThought(thought: Thought) {
            optionImage?.visibility=View.INVISIBLE
            username?.text = thought.userName
            thoughtsText?.text = thought.thoughtTxt
            numLikes?.text = thought.numLikes.toString()
            numComments?.text = thought.numComments.toString()
             timestamp?.text = thought.timestamp?.toDate().toString()
            itemView.setOnClickListener { itemClick(thought) }
            likesImage?.setOnClickListener {
                FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                    .document(thought.documentId)
                    .update(NUM_LIKES, thought.numLikes + 1)
            }
            if (FirebaseAuth.getInstance().currentUser.uid== thought.userId){
                optionImage?.visibility=View.VISIBLE
                optionImage?.setOnClickListener {
                    thoughtOptionListener.thoughtOptionMenuClicked(thought)
                }

            }
        }
    }
}