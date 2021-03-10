package com.seven.ontracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.toolbar.*


class ProfileActivity : AppCompatActivity() {
    private val authDb = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        // populate the textviews based on the current user's profile
        if (authDb.currentUser != null) {
            profileFullName.text = authDb.currentUser!!.displayName
            profileEmail.text = authDb.currentUser!!.email
        } else {
            FirebaseAuth.getInstance().signOut()
            finish()

            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
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
                startActivity(Intent(applicationContext, AddItemActivity::class.java))
                return true
            }

             R.id.action_list -> {
                 startActivity(Intent(applicationContext, DisplayItemActivity::class.java))

                 return true
             }
            R.id.action_profile -> {
                //already in this activity


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