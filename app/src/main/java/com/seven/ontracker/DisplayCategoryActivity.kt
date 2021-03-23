package com.seven.ontracker

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import io.grpc.InternalChannelz.id
import kotlinx.android.synthetic.main.activity_add_category.*
import kotlinx.android.synthetic.main.activity_display_category.*
import kotlinx.android.synthetic.main.toolbar.*
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

        setSupportActionBar(topToolbar)

    }
    // 2 overrides to display menu & handle its actions
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // inflate the main menu to add the items to the toolbar
        menuInflater.inflate(R.menu.navbar_menu, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // navigate menu items on click
        when (item.itemId) {
            R.id.action_add -> {
                startActivity(Intent(applicationContext, AddItemActivity::class.java))
                return true
            }

             R.id.action_list -> {
                 startActivity(Intent(applicationContext, DisplayItemActivity::class.java))
                 return true
             }
            R.id.action_profile -> {
                startActivity(Intent(applicationContext, ProfileActivity::class.java))
                return true


            }
            R.id.action_home -> {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                return true

            }
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                finish()

                val intent = Intent(applicationContext, SignInActivity::class.java)
                startActivity(intent)

            }
        }
        return super.onOptionsItemSelected(item)
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
            holder.itemView.updateItem.setOnClickListener {
                val intent = Intent(applicationContext, AddCategoryActivity::class.java)
                intent.putExtra("categoryName", model.categoryName)
                startActivity(intent)
                database.collection("categories").document(model.id.toString()).delete()
            }
            holder.itemView.deleteItem.setOnClickListener {
                database.collection("categories").document(model.id.toString()).delete()
            }
        }
    }
}