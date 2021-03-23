package com.seven.ontracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_display_question_answer.*
import kotlinx.android.synthetic.main.item_question.view.*

class DisplayQuestionAnswerActivity : AppCompatActivity() {
    // Firestore connection
    val database = FirebaseFirestore.getInstance()
    private var adapter: QAForumAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_question_answer)

        // Enforce our recycler view to use linear layout
        questionsRecyclerView.layoutManager = LinearLayoutManager(this)

        // create the query and sort the results by user name
        val query = database.collection("questions").orderBy("userName", Query.Direction.ASCENDING)

        // QAForumAdapter will grab the results and then display them in recycler view
        val options = FirestoreRecyclerOptions.Builder<QAForum>().setQuery(query, QAForum::class.java).build()
        adapter = QAForumAdapter(options)
        questionsRecyclerView.adapter = adapter

    }

    // Start listening for changes from firebase
    override fun onStart() {
        super.onStart()
        adapter!!.startListening()

        // check if user logged in or not
        val user = Firebase.auth.currentUser
        if (user == null) {
            // If user is not logged in then send user to the SignInActivity page
            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    // If there is nothing in firebase, then stop listening for chages
    override fun onStop() {
        super.onStop()
        if(adapter != null) {
            adapter!!.stopListening()
        }
    }

    // QAForumViewHolder class will bind our data in recycler view
    private inner class QAForumViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {

    }


    private inner class QAForumAdapter internal constructor(options: FirestoreRecyclerOptions<QAForum>) :
        FirestoreRecyclerAdapter<QAForum, QAForumViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QAForumViewHolder {
            // call the item_question view and render the recycler view
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_question, parent, false)
            return QAForumViewHolder(view)
        }

        // Saving username and question into the recycler view from our QAForum model for each occurence
        override fun onBindViewHolder(holder: QAForumViewHolder, position: Int, model: QAForum) {
            holder.itemView.userNameTextView.text = model.userName
            holder.itemView.questionTextView.text = model.question
        }
    }
}