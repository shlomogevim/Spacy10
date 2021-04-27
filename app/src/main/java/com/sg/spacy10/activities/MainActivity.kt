package com.sg.spacy10.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.sg.jhony50.ThoughtsAdapter
import com.sg.spacy10.R
import com.sg.spacy10.databinding.ActivityMainBinding
import com.sg.spacy10.interfaces.ThoughtOptionClickListener
import com.sg.spacy10.model.Thought
import com.sg.spacy10.utilities.*


class MainActivity : AppCompatActivity(),ThoughtOptionClickListener {
    private lateinit var binding: ActivityMainBinding
    lateinit var selectCategory: String
    lateinit var thoughtsAdapter: ThoughtsAdapter
    var thoughts = ArrayList<Thought>()
    val thoughtCollectionRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
    lateinit var thoughtsListener: ListenerRegistration
    lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        selectCategory = FUNNY
        binding.fab.setOnClickListener {
            var intent = Intent(this, AddThoughtActivity::class.java)
            startActivity(intent)
        }
        thoughtsAdapter = ThoughtsAdapter(thoughts,this) { thought ->
            var commentActivity = Intent(this, CommentsActivity::class.java)
            commentActivity.putExtra(DOCUMENT_KEY, thought.documentId)
            startActivity(commentActivity)
        }
        binding.thoughtListView.adapter = thoughtsAdapter
        val layoutManger = LinearLayoutManager(this)
        binding.thoughtListView.layoutManager = layoutManger


    }

    override fun onResume() {
        super.onResume()
        updateUi()
    }
    override fun thoughtOptionMenuClicked(thought: Thought) {
        Log.e(TAG,thought.thoughtTxt)
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val menuItem = menu.getItem(0)
        if (auth.currentUser == null) {
            //logout
            menuItem.title = "Login"
        } else {
            //login
            menuItem.title = "Logout"
            updateUi()
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_login) {
            if (auth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            } else {
                auth.signOut()
                updateUi()
            }
            return true
        }
        return false
    }

    fun updateUi() {
        if (auth.currentUser == null) {
            binding.mainCrazyBtn.isEnabled = false
            binding.mainPopularBtn.isEnabled = false
            binding.mainFunnyBtn.isEnabled = false
            binding.mainSeriousBtn.isEnabled = false
            binding.fab.isEnabled = false
            thoughts.clear()
            thoughtsAdapter.notifyDataSetChanged()
        } else {
            binding.mainCrazyBtn.isEnabled = true
            binding.mainPopularBtn.isEnabled = true
            binding.mainFunnyBtn.isEnabled = true
            binding.mainSeriousBtn.isEnabled = true
            binding.fab.isEnabled = true
            setListener()
        }
    }

    fun setListener() {
        if (selectCategory == POPULAR) {
            thoughtsListener = thoughtCollectionRef
                .orderBy(NUM_LIKES, Query.Direction.DESCENDING)
                .addSnapshotListener(this) { snapshot, exception ->
                    if (exception != null) {
                        Log.i(TAG, "cant retrive data :${exception.localizedMessage}")
                    }
                    if (snapshot != null) {
                        parseData(snapshot)

                    }
                }

        } else {
            thoughtsListener = thoughtCollectionRef
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .whereEqualTo(CATEGORY, selectCategory)
                .addSnapshotListener(this) { snapshot, exception ->
                    if (exception != null) {
                        Log.i(TAG, "cant retrive data :${exception.localizedMessage}")
                    }
                    if (snapshot != null) {
                        parseData(snapshot)
                    }
                }
        }
    }

    fun parseData(snapshot: QuerySnapshot) {
        thoughts.clear()
        for (document in snapshot.documents) {
            val data = document.data
            if (data != null) {
                val name = data[USERNAME] as String
                val timestamp = data[TIMESTAMP] as Timestamp
                var thoghtTxt = "ff"
                if (data[THOUGHT_TXT] != null) {
                    thoghtTxt = data[THOUGHT_TXT] as String
                }
                val numLikes = data[NUM_LIKES] as Long
                Log.i("message", "numLikes=$numLikes")
                val numComments = data[NUM_COMMENTS] as Long
                val documentId = document.id

                val userId = data[USER_ID] as String
                val newThought = Thought(
                    name, timestamp, thoghtTxt, numLikes.toInt(),
                    numComments.toInt(), documentId, userId
                )
                thoughts.add(newThought)
            }
            thoughtsAdapter.notifyDataSetChanged()
        }
    }

    fun mainFunnyOnClick(view: View) {
        binding.mainFunnyBtn.isChecked = true
        binding.mainSeriousBtn.isChecked = false
        binding.mainCrazyBtn.isChecked = false
        binding.mainPopularBtn.isChecked = false
        selectCategory = FUNNY
        thoughtsListener.remove()
        setListener()
    }

    fun mainSeriousOnclicked(view: View) {
        binding.mainFunnyBtn.isChecked = false
        binding.mainSeriousBtn.isChecked = true
        binding.mainCrazyBtn.isChecked = false
        binding.mainPopularBtn.isChecked = false
        selectCategory = SERIOUS
        thoughtsListener.remove()
        setListener()
    }

    fun mainCreazyOnclicked(view: View) {
        binding.mainFunnyBtn.isChecked = false
        binding.mainSeriousBtn.isChecked = false
        binding.mainCrazyBtn.isChecked = true
        binding.mainPopularBtn.isChecked = false
        selectCategory = CRAZY
        thoughtsListener.remove()
        setListener()
    }

    fun mainPopularOnclicked(view: View) {
        binding.mainFunnyBtn.isChecked = false
        binding.mainSeriousBtn.isChecked = false
        binding.mainCrazyBtn.isChecked = false
        binding.mainPopularBtn.isChecked = true
        selectCategory = POPULAR
        thoughtsListener.remove()
        setListener()
    }


}