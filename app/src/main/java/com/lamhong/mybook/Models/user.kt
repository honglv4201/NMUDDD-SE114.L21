package com.lamhong.mybook.Models

class User {
    private var fullname : String=""
    private var email: String=""
    private var uid: String=""
  //  private var imageUrl : String=""
    constructor()
    constructor( fullname : String,email: String, uid: String){
        this.fullname=fullname
        this.email=email
        this.uid=uid
       // this.imageUrl=imageUrl
    }
    fun getName(): String{
        return fullname
    }
    fun setName(fullname:String){
        this.fullname=fullname
    }
    fun getEmail(): String{
        return email;
    }
    fun setEmail(email : String){
        this.email=email;
    }
    fun getUid(): String{
        return uid
    }
    fun setUid(uid : String){
        this.uid=uid
    }
//    fun setImageurl(imageUrl: String){
//        this.imageUrl=imageUrl
//    }
//    fun getImageurl() : String{
//        return imageUrl
//    }

}