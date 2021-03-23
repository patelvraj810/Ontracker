package com.seven.ontracker

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.File
import java.io.FileInputStream


class ProfileActivity : AppCompatActivity() {
    private val authDb = FirebaseAuth.getInstance()
    // vars for profile photo
    private val REQUEST_CODE = 1000 // this is constant for the take picture intent in Android
    private lateinit var filePhoto: File
    private val FILE_NAME = "photo.jpg"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        // populate the textviews based on the current user's profile
        if (authDb.currentUser != null) {
            profileFullName.text = authDb.currentUser!!.displayName
            profileEmail.text = authDb.currentUser!!.email
            // display photoUrl if any
            var profilePhoto: Uri? = authDb.currentUser!!.photoUrl

            if (profilePhoto != null) {
                profilePhoto.path?.let { loadProfileImage(it) }
            }

        } else {
            FirebaseAuth.getInstance().signOut()
            finish()

            val intent = Intent(applicationContext, SignInActivity::class.java)
            startActivity(intent)
        }
        setSupportActionBar(topToolbar)

        // permission check for camera & storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (checkSelfPermission(
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED || checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED) || checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            //So if we don't have permission we request for permissions from the user, this will execute the overridden onRequestPermissionsResult
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )
        }

        // camera button click - try to take a picture
        profileImageView.setOnClickListener {
            // create an intent & generate a temp file
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            filePhoto = getPhotoFile(FILE_NAME)

            // invoke the intent and evaluate the result
            val providerFile = FileProvider.getUriForFile(this, "com.seven.ontracker.fileprovider", filePhoto)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)

            if (intent.resolveActivity(this.packageManager) != null) {
                startActivityForResult(intent, REQUEST_CODE) // execute the intent
            }
            else {
                Toast.makeText(this, "Camera could not open", Toast.LENGTH_LONG).show()
            }
        }

    }
    //Methods for profile image

    private fun getPhotoFile(fileName: String): File {
        // save a temp copy of the image to the Picture directory
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", directoryStorage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // if the intent successfully took a photo, convert the photo to a bitmap to dispaly in ImageView
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val takenPhoto = BitmapFactory.decodeFile(filePhoto.absolutePath)
            profileImageView.setImageBitmap(takenPhoto)

            // try saving the photoUri to the user's firebase profile for persistence
            // convert file path from a string to a uri before saving
            var builder = Uri.Builder()
            var localUri = builder.appendPath(filePhoto.absolutePath).build()
            saveProfilePhoto(localUri)
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    // save photo to user's firebase profile
    private fun saveProfilePhoto(imageUri: Uri?) {
        // set up the profile update to include the photo uri
        var profileUpdates = UserProfileChangeRequest.Builder()
            .setPhotoUri(imageUri)
            .build()

        // commit the update to firebase
        authDb.currentUser?.updateProfile(profileUpdates)?.addOnCompleteListener {
            OnCompleteListener<Void?> { p0 ->
                if (p0.isSuccessful) {
                    Toast.makeText(applicationContext, "Image saved to Firebase", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // load profile image if any
    private fun loadProfileImage(path: String) {
        var file: File = File(path)

        // convert to bitmap
        var bitmapImage = BitmapFactory.decodeStream(FileInputStream(file))

        // display in Image View
        profileImageView.setImageBitmap(bitmapImage)
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
            /*R.id.action_add -> {
                startActivity(Intent(applicationContext, Add_Activity::class.java))
                return true
            }*/

            /* R.id.action_list -> {
                 startActivity(Intent(applicationContext, Recycle_Activity::class.java))

                 return true
             }*/
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