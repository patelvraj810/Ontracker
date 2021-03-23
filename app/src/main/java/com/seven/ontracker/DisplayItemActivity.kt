package com.seven.ontracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_display_category.*
import kotlinx.android.synthetic.main.activity_display_category.categoriesRecyclearView
import kotlinx.android.synthetic.main.activity_display_item.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_category.view.*
import kotlinx.android.synthetic.main.view_item.view.*
import kotlinx.android.synthetic.main.view_item.view.deleteItem
import kotlinx.android.synthetic.main.view_item.view.updateItem

class DisplayItemActivity : AppCompatActivity() {
    // Firestore connection
    val database = FirebaseFirestore.getInstance()
    private var adapter: ItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_item)


        // Enforce our recycler view to use linear layout
        itemsRecyclearView.layoutManager = LinearLayoutManager(this)

        // create the query and sort the results by user name
        val query = database.collection("items").orderBy("itemName", Query.Direction.ASCENDING)

        // QAForumAdapter will grab the results and then display them in recycler view
        val options = FirestoreRecyclerOptions.Builder<Item>().setQuery(query, Item::class.java).build()
        adapter = ItemAdapter(options)
        itemsRecyclearView.adapter = adapter

        itemsRecyclearView.setVerticalScrollBarEnabled(true);

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
                //already in this activity

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

    private inner class ItemViewHolder internal constructor(private val view: View) : RecyclerView.ViewHolder(view) {

    }

    private inner class ItemAdapter internal constructor(options: FirestoreRecyclerOptions<Item>) :
        FirestoreRecyclerAdapter<Item, ItemViewHolder>(options) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            // call the item_question view and render the recycler view
            val view = LayoutInflater.from(parent.context).inflate(R.layout.view_item, parent, false)
            return ItemViewHolder(view)
        }

        // passing itemName,itemLocation and itemDescription into the recycler view from our form model for each occurence
        override fun onBindViewHolder(holder: ItemViewHolder, position: Int, model: Item) {
            holder.itemView.itemNameTextView.text = model.itemName
            holder.itemView.itemLocationTextView.text = model.itemLocation
            holder.itemView.itemDescriptionTextView.text = model.itemDescription

            // Item selection when RecyclerView item touched
            holder.itemView.itemNameTextView.setOnClickListener {
                val intent = Intent(applicationContext, DetailsItemActivity::class.java)
                intent.putExtra("id", model.id)
                intent.putExtra("itemName", model.itemName)
                intent.putExtra("itemLocation", model.itemLocation)
                intent.putExtra("itemDescription", model.itemDescription)
                intent.putExtra("itemImage", model.itemImage)
                startActivity(intent)
            }
            holder.itemView.updateItem.setOnClickListener {
                val intent = Intent(applicationContext, AddItemActivity::class.java)
                intent.putExtra("categoryName", model.itemName)
                startActivity(intent)
                database.collection("items").document(model.id.toString()).delete()
            }
            holder.itemView.deleteItem.setOnClickListener {
                database.collection("items").document(model.id.toString()).delete()
            }
        }
    }
}