package com.example.instagram.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.instagram.AccountSettingsActivity
import com.example.instagram.Model.User
import com.example.instagram.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class AccountFragment : Fragment() {

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var folloewes: TextView
    private lateinit var followings: TextView
    private lateinit var edit: TextView
    private lateinit var avatar: ImageView
    private lateinit var uName: TextView
    private lateinit var bio: TextView
    private lateinit var email: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        edit = view.findViewById(R.id.edit_account)
        avatar = view.findViewById(R.id.avatar)
        uName = view.findViewById(R.id.uName)
        email = view.findViewById(R.id.email)
        bio = view.findViewById(R.id.bio)
        folloewes = view.findViewById(R.id.totalFollowers)
        followings = view.findViewById(R.id.totalFollowings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if (profileId == firebaseUser.uid){

        }else if (profileId != firebaseUser.uid){
            checkFollowAndFollowing()
        }

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val userReff: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)

        email.text = FirebaseAuth.getInstance().currentUser!!.email.toString()
        Glide.with(context).load("https://i.redd.it/vqfe518ghnn31.jpg").into(avatar)
        userInfo()
        getFollowers()
        getFollowing()

        edit.setOnClickListener {
            val getButtonText = edit.text.toString()
            when{
//                ==================================================================================
                getButtonText == "Edit" -> startActivity(Intent(context, AccountSettingsActivity::class.java))
//                ==================================================================================
                getButtonText == "Follow" -> {
                    edit.text = "Following"
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(it1.toString())
                                .child("Following")
                                .child(profileId).setValue(true)
                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow")
                                .child(profileId)
                                .child("Followers")
                                .child(it1.toString()).setValue(true)
                    }
                }
//                ==================================================================================
                getButtonText == "Following" -> {
                    edit.text = "Follow"
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow")
                                .child(it1.toString())
                                .child("Following")
                                .child(profileId).removeValue()
                    }

                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                                .child("Follow").child(profileId)
                                .child("Followers")
                                .child(it1.toString()).removeValue()
                    }
                }
//                ==================================================================================

            }

        }

        return view
    }

    private fun checkFollowAndFollowing() {
        val folllowingRef = FirebaseDatabase.getInstance().reference
                    .child("Follow")
                    .child(profileId)
                    .child("Followers")
            folllowingRef.addValueEventListener(object : ValueEventListener{
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.child(FirebaseAuth.getInstance().currentUser?.uid.toString()).exists()){
                            edit.text = "Following"
                    }else{
                            edit.text = "Follow"
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })

    }

    private fun getFollowers(){
        val folllowersRef = FirebaseDatabase.getInstance().reference
                    .child("Follow")
                    .child(profileId)
                    .child("Followers")

        folllowersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    folloewes.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun getFollowing(){
        val folllowersRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                    .child("Follow")
                    .child(profileId)
                    .child("Following")
        }
        folllowersRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    followings.text = snapshot.childrenCount.toString()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun userInfo(){
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
//                if (context != null){
//                    return
//                }
                if (snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Glide.with(activity).load(user!!.getImage()).into(avatar)
                    uName.text = user!!.getFullName()
                    bio.text = user!!.getBio()
                    email.text = user!!.getEmail()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onStop() {
        super.onStop()
        val  pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val  pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val  pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}