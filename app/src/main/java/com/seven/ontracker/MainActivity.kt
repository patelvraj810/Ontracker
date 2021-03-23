package com.seven.ontracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addItemBtn.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        viewItemBtn.setOnClickListener {
            val intent = Intent(this, DisplayItemActivity::class.java)
            startActivity(intent)
        }

        addCategoryBtn.setOnClickListener {
            val intent = Intent (this, AddCategoryActivity::class.java)
            startActivity(intent)
        }

        viewCategoryBtn.setOnClickListener {
            val intent = Intent (this, DisplayCategoryActivity::class.java)
            startActivity(intent)
        }

        aboutUsBtn.setOnClickListener {
            val intent = Intent (this, AboutUsActivity::class.java)
            startActivity(intent)
        }

        forumButton.setOnClickListener {
            val intent = Intent (this, QuestionAnswerActivity::class.java)
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
                startActivity(Intent(applicationContext, ProfileActivity::class.java))


            }
            R.id.action_home -> {
                //already at home
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