package com.example.instagram.Model

class Post {
    private var description: String = ""
    private var postid: String = ""
    private var postimage: String = ""
    private var publisher: String = ""

    constructor()
    constructor(description: String, postid: String, postimage: String, publisher: String) {
        this.description = description
        this.postid = postid
        this.postimage = postimage
        this.publisher = publisher
    }

    fun getPostId(): String{
        return postid
    }

    fun getDescription(): String{
        return description
    }
    fun getPublisher(): String{
        return publisher
    }
    fun getPostImage(): String{
        return postimage
    }

}