package com.seven.ontracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_category.*
import kotlinx.android.synthetic.main.toolbar.*

class AddCategoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

        saveBtn.setOnClickListener{
            if ((!TextUtils.isEmpty(editTextName.text))) {
                // capture inputs into an instance of our Restaurant class
                val addCategory = category()
                addCategory.itemName = editTextName.text.toString().trim()

                // connect & save to Firebase. collection will be created if it doesn't exist already
                val db = FirebaseFirestore.getInstance().collection("addCategory")
                addCategory.id = db.document().id
                db.document(addCategory.id!!).set(addCategory)

                // show confirmation & clear inputs
                editTextName.setText("")
                Toast.makeText(this, "Category Added", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, "Incomplete", Toast.LENGTH_LONG).show()
            }
        }
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
               //add item
            }

            /* R.id.action_list -> {
                 startActivity(Intent(applicationContext, Recycle_Activity::class.java))

                 return true
             }*/
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
}