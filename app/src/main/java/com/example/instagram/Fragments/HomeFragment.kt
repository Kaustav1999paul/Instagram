package com.example.instagram.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.instagram.AddPostActivity
import com.example.instagram.R


class HomeFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_home, container, false)

        val newPost = view.findViewById<ImageView>(R.id.newPost)
        newPost.setOnClickListener {
            startActivity(Intent(activity, AddPostActivity::class.java))
        }

        return view
    }
}