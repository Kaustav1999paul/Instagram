package com.example.instagram.Model

class Comment {
    private var comment: String = ""
    private var publisher: String = ""

    constructor()

    constructor(comment: String, publisher: String) {
        this.comment = comment
        this.publisher = publisher
    }

    fun getComment(): String{
        return comment
    }

    fun getPublisher(): String{
        return publisher
    }
}