package com.example.instagram

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.instagram.Model.Post
import com.example.instagram.Model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FullScreenImage : AppCompatActivity() {

    private var image = ""
    private var postId = ""
    private lateinit var imageHolder: ImageView
    private lateinit var cross: ImageView
    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        cross = findViewById(R.id.cross)
        imageHolder = findViewById(R.id.fullImage)
        title = findViewById(R.id.titleFM)
        image = intent.getStringExtra("image").toString()
        postId = intent.getStringExtra("idPost").toString()

        cross.setOnClickListener { finish() }
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts").child(postId)
        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val post = snapshot.getValue(Post::class.java)
                    title.text = post?.getDescription()
                    Glide.with(imageHolder).load(post?.getPostImage()).into(imageHolder)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    }
}