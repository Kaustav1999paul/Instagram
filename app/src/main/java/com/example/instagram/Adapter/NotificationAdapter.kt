package com.example.instagram.Adapter

import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagram.FullScreenImage
import com.example.instagram.Model.Notification
import com.example.instagram.Model.Post
import com.example.instagram.Model.User
import com.example.instagram.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jetbrains.annotations.NotNull

class NotificationAdapter(private var mContext: Context,
                          private var mNotif: List<Notification>) : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.notif_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val notification = mNotif[position]
        // Fetch data and show them here

        if (notification.getText().equals("started following you")){
            holder.textNotif.text = "started following you"
        }else if (notification.getText().equals("liked your post")){
            holder.textNotif.text = "liked your post"
        }else if (notification.getText().contains("Commented:")){
            holder.textNotif.text = notification.getText().replace("Commented:", "Commented: ")
        }else{
            holder.textNotif.text = notification.getText()
        }

        userInfo(holder.profileImage, holder.nameNotif, notification.getUserId())
        postImg(holder.postImage, notification.getPostId())

        holder.postImage.setOnClickListener {
            var intent = Intent(mContext, FullScreenImage::class.java)
            intent.putExtra("idPost", notification.getPostId())
            mContext.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return mNotif.size
    }


    inner class ViewHolder(@NotNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var profileImage: ImageView
        var postImage: ImageView
        var nameNotif: TextView
        var textNotif: TextView


        init {
            profileImage = itemView.findViewById(R.id.notifProfileImage)
            postImage = itemView.findViewById(R.id.postImg)
            nameNotif = itemView.findViewById(R.id.notifName)
            textNotif = itemView.findViewById(R.id.notif)

        }
    }

    private fun userInfo(imageView: ImageView, fullName: TextView, publisherId: String){
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                if (context != null){
//                    return
//                }
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Glide.with(imageView).load(user!!.getImage()).into(imageView)
                    fullName.text = user!!.getFullName()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun postImg(imageView: ImageView, postID: String){
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
            .child(postID)

        postRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val post = snapshot.getValue(Post::class.java)

                    Glide.with(imageView).load(post?.getPostImage()).into(imageView)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}