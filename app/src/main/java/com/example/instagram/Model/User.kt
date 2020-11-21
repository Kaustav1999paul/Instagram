package com.example.instagram.Model

class User {
    private var bio: String = ""
    private var email: String = ""
    private var fullname: String = ""
    private var image: String = ""
    private var uid: String = ""
    private var username: String = ""

    constructor()

    constructor(bio: String,email: String,fullname: String,image: String,uid: String,username: String){
        this.bio = bio
        this.fullname = fullname
        this.username  = username
        this.uid = uid
        this.image = image
        this.email = email
    }

    fun getUserName() : String{
        return username
    }
    fun getFullName() : String{
        return fullname
    }
    fun getEmail() : String{
        return email
    }
    fun getUid() : String{
        return uid
    }
    fun getImage() : String{
        return image
    }
    fun getBio() : String{
        return bio
    }
}