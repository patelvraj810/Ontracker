package com.seven.ontracker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.Spinner
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_details_item.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.IOException
import java.util.*

class AddItemActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    val item = Item()
    var downloadUri :Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        //the below code is for image selection
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        imageBtn.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        }

        saveItemBtn.setOnClickListener{
            if ((!TextUtils.isEmpty(itemNameEditText.text)) && (!TextUtils.isEmpty(itemLocationEditText.text)) && (!TextUtils.isEmpty(itemDescriptionEditText.text))
            ) {

                if(filePath != null){
                    val itemImageID = UUID.randomUUID().toString()
                    val ref = storageReference?.child("uploads/$itemImageID")
                    val uploadTask = ref?.putFile(filePath!!)

                    val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        return@Continuation ref.downloadUrl
                    })?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveToDb(itemImageID)
                        } else {
                            // Handle failures
                        }
                    }?.addOnFailureListener{

                    }
                }else{
                    Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
                }

                val intent = Intent(applicationContext, DisplayItemActivity::class.java)
                startActivity(intent)

            }
            else {
                Toast.makeText(this, "Try Again", Toast.LENGTH_LONG).show()
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

    private fun saveToDb(uri: String){

        // capture inputs into an instance of our item class
        val item = Item()
        item.itemName = itemNameEditText.text.toString().trim()
        item.itemLocation = itemLocationEditText.text.toString().trim()
        item.itemDescription = itemDescriptionEditText.text.toString().trim()
        item.itemImage = uri


        // connect & save to Firebase. collection will be created if it doesn't exist already
        val db = FirebaseFirestore.getInstance().collection("items")
        item.id = db.document().id
        db.document(item.id!!).set(item)

        // show confirmation & clear inputs
        itemNameEditText.setText("")
        itemLocationEditText.setText("")
        itemDescriptionEditText.setText("")


        Toast.makeText(this, "Item Added", Toast.LENGTH_LONG).show()


    }

    //the below method is for selecting image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imageBtn.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}