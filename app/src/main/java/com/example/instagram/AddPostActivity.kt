package com.example.instagram

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.github.jorgecastilloprz.FABProgressCircle
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage

class AddPostActivity : AppCompatActivity() {

    private lateinit var fabProgressCircle: FABProgressCircle
    private lateinit var fabView: FloatingActionButton
    private lateinit var cancelPost: FloatingActionButton
    private lateinit var imageSelect: ImageView
    private lateinit var postTitle: EditText
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPictureRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)
        imageSelect = findViewById(R.id.imageSelect)
        postTitle = findViewById(R.id.postTitle)

        CropImage.activity().setAspectRatio(4,2)
            .start(this@AddPostActivity)

        storagePostPictureRef = FirebaseStorage.getInstance().reference.child("Posts Pictures")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w: Window = window
            w.setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            )
        }
        fabProgressCircle = findViewById(R.id.fabProgressCircle)
        fabView = findViewById(R.id.fab)
        cancelPost = findViewById(R.id.cancelPost)
        cancelPost.setOnClickListener { finish() }

        fabView.setOnClickListener {
            uploadImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            imageSelect.setImageURI(imageUri)
        }
    }

    private fun uploadImage() {

        when{
            imageUri == null -> Toast.makeText(this, "Please Select image", Toast.LENGTH_SHORT).show()
            postTitle.text.toString() == "" -> Toast.makeText(this, "Title Empty", Toast.LENGTH_SHORT).show()
            else -> {
                cancelPost.isEnabled = false
                cancelPost.isClickable = false
                fabProgressCircle.show()
                val fileRef = storagePostPictureRef!!.child(System.currentTimeMillis().toString() + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)

                uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Posts")

                        val postId = ref.push().key
                        val userMap = HashMap<String, Any>()

                        userMap["postid"] = postId!!
                        userMap["description"] = postTitle.text.toString()
                        userMap["publisher"] = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        userMap["postimage"] = myUrl
                        ref.child(postId).updateChildren(userMap).addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                fabProgressCircle.beginFinalAnimation()
                                finish()
                                Toast.makeText(this, "Account Updated", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}