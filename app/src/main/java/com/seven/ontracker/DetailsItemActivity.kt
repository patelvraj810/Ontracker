package com.seven.ontracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_details_item.*
import kotlinx.android.synthetic.main.toolbar.*

class DetailsItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_item)

        // populate Heading textview item details

        itemName.setText(intent.getStringExtra("itemName"))
        itemLocation.setText(intent.getStringExtra("itemLocation"))
        itemDescription.setText(intent.getStringExtra("itemDescription"))

//shows top tool bar
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


            }
            R.id.action_home -> {
                startActivity(Intent(applicationContext, MainActivity::class.java))
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