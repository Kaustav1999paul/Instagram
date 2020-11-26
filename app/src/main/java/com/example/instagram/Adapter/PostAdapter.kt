package com.example.instagram.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagram.CommentsActivity
import com.example.instagram.FullScreenImage
import com.example.instagram.Model.Post
import com.example.instagram.Model.User
import com.example.instagram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jetbrains.annotations.NotNull

class PostAdapter(private var mContext: Context,
                  private var mPost: List<Post>) : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.post_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = mPost[position]
        Glide.with(holder.postImage).load(post.getPostImage()).into(holder.postImage)
        holder.postTitle.text = post.getDescription()
        getPublisherImfo(holder.profileImage, holder.namePost, post.getPublisher())

        holder.postImage.setOnClickListener {
            var intent = Intent(mContext, FullScreenImage::class.java )
            intent.putExtra("image", post.getPostImage())
            intent.putExtra("idPost", post.getPostId())
            mContext.startActivity(intent)
        }

        holder.comment.setOnClickListener {
            var intent = Intent(mContext, CommentsActivity::class.java)
            intent.putExtra("postId", post.getPostId())
            intent.putExtra("publisherId", post.getPublisher())
            mContext.startActivity(intent)
        }

        isLikes(post.getPostId(), holder.liked)
        holder.liked.setOnClickListener {
            if (holder.liked.tag == "Like"){
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.getPostId())
                    .child(firebaseUser!!.uid)
                    .setValue(true)

                addNotifications(post.getPublisher(), post.getPostId())
            }else{
                FirebaseDatabase.getInstance().reference.child("Likes")
                    .child(post.getPostId())
                    .child(firebaseUser!!.uid)
                    .removeValue()
            }
        }
        numberOfLikes(holder.likeCount, post.getPostId())
    }

    private fun numberOfLikes(likeCount: TextView, postId: String) {
        val LikesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(postId)

        LikesRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(po: DataSnapshot) {
                if (po.exists()){
                   likeCount.text = po.childrenCount.toString() + " Likes"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun isLikes(postId: String, liked: ImageView) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val LikesRef = FirebaseDatabase.getInstance().reference.child("Likes").child(postId)

        LikesRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(po: DataSnapshot) {
                if (po.child(firebaseUser!!.uid).exists()){
                    liked.setImageResource(R.drawable.ic_liked)
                    liked.tag = "Liked"
                }else{
                    liked.setImageResource(R.drawable.ic_round_favorite_border_24)
                    liked.tag = "Like"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun getPublisherImfo(profileImage: ImageView, namePost: TextView, publisher: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisher)

        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    Glide.with(profileImage).load(user!!.getImage()).into(profileImage)
                    namePost.text = user!!.getFullName()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })



    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    inner class ViewHolder(@NotNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var profileImage: ImageView
        var postImage: ImageView
        var namePost: TextView
        var postTitle: TextView
        var liked: ImageView
        var comment: ImageView
        var likeCount: TextView

        init {
            profileImage = itemView.findViewById(R.id.avatarPost)
            postImage = itemView.findViewById(R.id.postImage)
            namePost = itemView.findViewById(R.id.namePost)
            postTitle = itemView.findViewById(R.id.titlePost)
            liked = itemView.findViewById(R.id.liked)
            comment = itemView.findViewById(R.id.commentButton)
            likeCount = itemView.findViewById(R.id.likeCount)
        }
    }

    private fun addNotifications(userId: String, postId: String) {
        val notifRef = FirebaseDatabase.getInstance().reference
            .child("Notifications").child(userId)

        val notiMap = HashMap<String, Any>()
        notiMap["userId"] = firebaseUser!!.uid
        notiMap["text"] = "liked your post"
        notiMap["postId"] = postId
        notiMap["isPost"] = true

        notifRef.push().setValue(notiMap)
    }
}