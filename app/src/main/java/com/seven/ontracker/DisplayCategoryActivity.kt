package com.seven.ontracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_display_category.*
import kotlinx.android.synthetic.main.view_category.view.*

class DisplayCategoryActivity : AppCompatActivity() {
    // Firestore connection
    val database = FirebaseFirestore.getInstance()
    private var adapter: CategoryAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_category)


        // Enforce our recycler view to use linear layout
        categoriesRecyclearView.layoutManager = LinearLayoutManager(this)

        // create the query and sort the results by user name
        val query = database.collection("categories").orderBy("categoryName", Query.Direction.ASCENDING)

        // QAForumAdapter will grab the results and then display them in recycler view
        val options = FirestoreRecyclerOptions.Builder<Category>().setQuery(query, Category::class.java).build()
        adapter = CategoryAdapter(options)
        categoriesRecyclearView.adapter = adapter

        categoriesRecyclearView.setVerticalScrollBarEnabled(true);
    }

    // Start listening for changes from firebase
    override fun onStart() {
        super.onStart()
        adapter!!.startListening()

        // check if user logged in or not
        val user = Firebase.auth.currentUser
    }

    // If there is nothing in firebase, then stop listening for chages
    override fun onStop() {
        super.onStop()
        if(adapter != null) {
            adapter!!.stopListening()
        }
    }

    private inner class CategoryViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {

    }

    private inner class CategoryAdapter internal constructor(options: FirestoreRecyclerOptions<Category>) :
        FirestoreRecyclerAdapter<Category, CategoryViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            // call the item_question view and render the recycler view
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_category, parent, false)
            return CategoryViewHolder(view)
        }

        // Saving username and question into the recycler view from our QAForum model for each occurence
        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int, model: Category) {
            holder.itemView.categoryNameTextView.text = model.categoryName
        }
    }
}