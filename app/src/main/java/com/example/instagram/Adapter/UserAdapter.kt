package com.example.instagram.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagram.Model.User
import com.example.instagram.R
import com.example.instagram.SignInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jetbrains.annotations.NotNull

class UserAdapter (private var mContext: Context,
                   private var mUser: List<User>,
                   private var isFragment: Boolean = false) : RecyclerView.Adapter<UserAdapter.ViewHolder>()
{
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout, parent, false)

        return UserAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user = mUser[position]
        holder.userNameTV.text = user.getUserName()
        holder.userFullNameTV.text = user.getFullName()
        Glide.with(holder.userAvatarIV).load(user.getImage()).into(holder.userAvatarIV)

        holder.itemView.setOnClickListener {
            val  pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
            pref.putString("profileId", user.getUid())
            pref.apply()
        }

        checkFollowStatus(user.getUid(), holder.followButton)
        holder.followButton.setOnClickListener {
            if (holder.followButton.text.toString() == "Follow"){
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                            .child("Follow")
                            .child(it1.toString())
                            .child("Following").child(user.getUid())
                            .setValue(true).addOnCompleteListener {task ->
                                if (task.isSuccessful){
                                    firebaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                                .child("Follow").child(user.getUid())
                                                .child("Followers").child(it1.toString())
                                                .setValue(true).addOnCompleteListener {task ->
                                                    if (task.isSuccessful){
                                                        //  send notifications

                                                    }
                                                }
                                    }
                                }
                            }
                }
            }else{
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                            .child("Follow")
                            .child(it1.toString())
                            .child("Following").child(user.getUid())
                            .removeValue().addOnCompleteListener {task ->
                                if (task.isSuccessful){
                                    firebaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                                .child("Follow").child(user.getUid())
                                                .child("Followers").child(it1.toString())
                                                .removeValue().addOnCompleteListener {task ->
                                                    if (task.isSuccessful){
                                                        // send notifications

                                                    }
                                                }
                                    }
                                }
                            }
                }
            }
        }
    }

    private fun checkFollowStatus(uid: String, followButton: TextView) {
       val folllowingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                    .child("Follow")
                    .child(it1.toString())
                    .child("Following")
       }
        folllowingRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(uid).exists()){
                    followButton.text = "Following"
                }else{
                    followButton.text = "Follow"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun getItemCount(): Int {
        return mUser.size
    }


    class ViewHolder (@NotNull itemView: View) : RecyclerView.ViewHolder(itemView){
        var userNameTV : TextView = itemView.findViewById(R.id.nameUserName)
        var userFullNameTV : TextView = itemView.findViewById(R.id.nameUserFullName)
        var followButton : TextView = itemView.findViewById(R.id.followButton)
        var userAvatarIV : ImageView = itemView.findViewById(R.id.avatarUser)
    }
}