 package com.seven.ontracker

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_category.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.view_category.view.*
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class AddCategoryActivity : AppCompatActivity(){

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    val category = Category()
//    var downloadUri :Uri? = null
    private var isEditMode = false

    private var id: String? = null
    private var categoryName: String? = null
    private var categoryImage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_category)

      //  isEditMode = intent.getBooleanExtra("isEditMode", false);

        //the below code is for image selection
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        imageBtn.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view:View) {
                selectImage(this@AddCategoryActivity)
            }
        })

        //the below is edit code
//        if (isEditMode) {
//            //update data
//            id = intent.getStringExtra("ID");
//            categoryName = intent.getStringExtra("CATEGORY_NAME");
//            categoryImage = intent.getStringExtra("CATEGORY_IMAGE");
//
//            categoryNameEditText.setText(id)
//        }

//        imageBtn.setOnClickListener {
//            val intent = Intent()
//            intent.type = "image/*"
//            intent.action = Intent.ACTION_GET_CONTENT
//            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
//        }

        saveBtn.setOnClickListener{

            //saving image
            //saving to name firestore
            if ((!TextUtils.isEmpty(categoryNameEditText.text))) {
                // capture inputs into an instance of our category class

                if(filePath != null){
                    category.categoryName = categoryNameEditText.text.toString().trim()
                    val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
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
                           val downloadUri = task.result
                            category.categoryImage = downloadUri.toString()

                            // connect & save to Firebase. collection will be created if it doesn't exist already
                            val db = FirebaseFirestore.getInstance().collection("categories")
                            category.id = db.document().id
                            db.document(category.id!!).set(category)
//
                            categoryNameEditText.setText("")
                            Toast.makeText(this, "Category Added", Toast.LENGTH_LONG).show()
                            val intent = Intent(applicationContext, DisplayCategoryActivity::class.java)
                            startActivity(intent)
                        } else {
                            // Handle failures
                        }
                    }?.addOnFailureListener{

                    }
                }else{
                    Toast.makeText(this, "Please Upload an Image", Toast.LENGTH_SHORT).show()
                }

//                category.categoryName = categoryNameEditText.text.toString().trim()
////             category.categoryImage = downloadUri.toString()
//                // connect & save to Firebase. collection will be created if it doesn't exist already
//                val db = FirebaseFirestore.getInstance().collection("categories")
//                category.id = db.document().id
//                db.document(category.id!!).set(category)

                // show confirmation & clear inputs
                
            }
            else {
                Toast.makeText(this, "Try Again", Toast.LENGTH_LONG).show()
            }

        }
        //the below line is for toolbar
        setSupportActionBar(topToolbar)

    }

    private fun selectImage(context: Context) {
        val options = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options, object: DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, item:Int) {
                if (options[item] == "Take Photo")
                {
                    val takePicture = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(takePicture, 0)
                }
                else if (options[item] == "Choose from Gallery")
                {
                    val pickPhoto = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(pickPhoto, 1)
                }
                else if (options[item] == "Cancel")
                {
                    dialog.dismiss()
                }
            }
        })
        builder.show()
    }


//    private fun saveToDb(uri: String){
//        category.categoryName = categoryNameEditText.text.toString().trim()
//        category.categoryImage = uri
//        // connect & save to Firebase. collection will be created if it doesn't exist already
//        val db = FirebaseFirestore.getInstance().collection("categories")
//        category.id = db.document().id
//        db.document(category.id!!).set(category)
//
//        // show confirmation & clear inputs
//        categoryNameEditText.setText("")
//        Toast.makeText(this, "Category Added", Toast.LENGTH_LONG).show()
//
//    }

    //the below method is for selecting image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
//            if(data == null || data.data == null){
//                return
//            }
//
//            filePath = data.data
//            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
//                imageBtn.setImageBitmap(bitmap)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_CANCELED) {
            when (requestCode) {
                0 -> if (resultCode == RESULT_OK && data != null) {
                    filePath = data.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                        imageBtn.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }

//                    filePath = data.data
////                    val selectedImage = data.getExtras()?.get("data") as Bitmap
////                    imageBtn.setImageBitmap(selectedImage)
//                    val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
//                    imageBtn.setImageBitmap(bitmap)

                1 -> if (resultCode == RESULT_OK && data != null) {

                    //  val filePathColumn = arrayOf<String>(MediaStore.Images.Media.DATA)
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
    }

    //the below two methods are for toolbar
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
}
