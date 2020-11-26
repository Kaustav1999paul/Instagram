package com.example.instagram.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.instagram.FullScreenImage
import com.example.instagram.Model.Post
import com.example.instagram.R

class MyImagesAdapter(private val mContext: Context, private var mPost: List<Post>): RecyclerView.Adapter<MyImagesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.images_item_layout, parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var iamge = mPost!!.get(position)
        Glide.with(holder.imageView).load(iamge.getPostImage()).into(holder.imageView)
        holder.imageView.setOnClickListener {
            var intent = Intent(mContext, FullScreenImage::class.java )
            intent.putExtra("image", iamge.getPostImage())
            intent.putExtra("idPost", iamge.getPostId())
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return mPost!!.size
    }

    inner class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView){
        var imageView: ImageView
        init {
            imageView = itemView.findViewById(R.id.iamgeMy)
        }
    }
}