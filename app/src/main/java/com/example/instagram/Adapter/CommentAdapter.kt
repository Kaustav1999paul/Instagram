package com.example.instagram.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagram.Model.Comment
import com.example.instagram.Model.User
import com.example.instagram.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentAdapter(private var mContext: Context, private var mComment: MutableList<Comment>?):
        RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.comments_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        var comment = mComment!!.get(position)
        getUserInfo(holder.senderImage, holder.senderName, comment.getPublisher())
        holder.senderComment.text = comment.getComment()
    }

    private fun getUserInfo(senderImage: ImageView, senderName: TextView, publisher: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisher)

        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    senderName.text = user!!.getFullName()
                    Glide.with(senderImage).load(user.getImage()).into(senderImage)
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    override fun getItemCount(): Int {
        return mComment!!.size
    }

    inner class ViewHolder(@NonNull itmeView: View) : RecyclerView.ViewHolder(itmeView){

        var senderImage: ImageView
        var senderName: TextView
        var senderComment: TextView

        init {
            senderImage = itmeView.findViewById(R.id.senderImage)
            senderComment = itemView.findViewById(R.id.senderComment)
            senderName = itemView.findViewById(R.id.senderName)
        }
    }
}