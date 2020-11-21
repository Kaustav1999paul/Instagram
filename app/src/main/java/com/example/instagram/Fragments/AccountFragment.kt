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
import com.example.instagram.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AccountFragment : Fragment() {

    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)
        val edit = view.findViewById<TextView>(R.id.edit_account)
        val avatar = view.findViewById<ImageView>(R.id.avatar)
        val uName = view.findViewById<TextView>(R.id.uName)
        val email = view.findViewById<TextView>(R.id.email)
        val bio = view.findViewById<TextView>(R.id.bio)
        edit.setOnClickListener {
            startActivity(Intent(context, AccountSettingsActivity::class.java))
        }


        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null){
            this.profileId = pref.getString("profileId", "none").toString()
        }

        if (profileId == firebaseUser.uid){
            edit.text = "Edit Profile"
        }else if (profileId != firebaseUser.uid){
            checkFollowAndFollowing()
        }

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
        val userReff: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(currentUserId)

        email.text = FirebaseAuth.getInstance().currentUser!!.email.toString()
        Glide.with(context).load("https://i.redd.it/vqfe518ghnn31.jpg").into(avatar)
        return view
    }

    private fun checkFollowAndFollowing() {
        val folllowingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                    .child("Follow")
                    .child(it1.toString())
                    .child("Following")
        }
    }
}