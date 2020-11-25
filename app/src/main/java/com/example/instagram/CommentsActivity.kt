package com.example.instagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.instagram.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentsActivity : AppCompatActivity() {

    private lateinit var currentUserPhoto: ImageView
    private lateinit var back: ImageView
    private var postId = ""
    private var publisherId = ""
    private lateinit var postButton: TextView
    private lateinit var commentEdit: EditText
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        commentEdit = findViewById(R.id.commentEdit)
        postButton = findViewById(R.id.commentPost)
        currentUserPhoto = findViewById(R.id.currentUser)
        back = findViewById(R.id.back)
        back.setOnClickListener { finish() }
        userInfo()
//        ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

        val intent = intent
        postId = intent.getStringExtra("postId").toString()
        publisherId = intent.getStringExtra("publisherId").toString()

        postButton.setOnClickListener {
            val text = commentEdit.text.trim().toString()
            if (text == ""){
                Toast.makeText(this@CommentsActivity, "No comments typed", Toast.LENGTH_SHORT).show()
            }else{
                addComments(text)
            }
        }

    }

    private fun addComments(text: String) {
        val commentRef = FirebaseDatabase.getInstance().reference.child("Comments").child(postId)

        val commentMap = HashMap<String, Any>()
        commentMap["comment"] = text
        commentMap["publisher"] = firebaseUser!!.uid

        commentRef.push().setValue(commentMap)
        commentEdit!!.text.clear()

    }
    private fun userInfo(){
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Glide.with(currentUserPhoto).load(user!!.getImage()).into(currentUserPhoto)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}