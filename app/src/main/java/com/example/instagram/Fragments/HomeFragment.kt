package com.example.instagram.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instagram.Adapter.PostAdapter
import com.example.instagram.AddPostActivity
import com.example.instagram.Model.Post
import com.example.instagram.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {

    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_home)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager


        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        checkFollowings()

        return view
    }

    private fun checkFollowings() {
        followingList = ArrayList()
        val folllowingRef = FirebaseDatabase.getInstance().reference
                    .child("Follow")
                    .child(FirebaseAuth.getInstance().currentUser!!.uid)
                    .child("Following")

        folllowingRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(po: DataSnapshot) {
                if (po.exists()){
                    (followingList as ArrayList<String>).clear()

                    for (snapshot in po.children){
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    retreavePosts()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun retreavePosts() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(po: DataSnapshot) {
                postList?.clear()
                for (snapshot in po.children){
                    val post = snapshot.getValue(Post::class.java)

                    for (userd in (followingList as ArrayList<String>)){
                        if (post!!.getPublisher().equals(userd)){
                            postList!!.add(post)
                        }
                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}