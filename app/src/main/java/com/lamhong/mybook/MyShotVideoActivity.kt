package com.lamhong.mybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.mybook.Adapter.ImageProfileAdapter
import com.lamhong.mybook.Adapter.ShotVideoListAdapter
import com.lamhong.mybook.Models.Post
import com.lamhong.mybook.Models.ShortVideo

class MyShotVideoActivity : AppCompatActivity() {

    private var shotList : List<ShortVideo> = ArrayList()
    private var shotvideoListAdapter : ShotVideoListAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_shot_video)
        var recycleview :RecyclerView
        getShot()

        recycleview=    findViewById(R.id.recycleview_listShotVideo)
        var gridLayoutManager= GridLayoutManager(this, 3)
        recycleview.layoutManager= gridLayoutManager
        shotvideoListAdapter= this?.let { ShotVideoListAdapter(it, shotList as ArrayList) }
        recycleview.adapter=shotvideoListAdapter

    }

    private fun getShot(){
        val postRef= FirebaseDatabase.getInstance().reference.child("ShotVideos")
        postRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (shotList as ArrayList<ShortVideo>).clear()
                    for (s in snapshot.children){
                        val shot= s.getValue(ShortVideo::class.java)
                        shot!!.setVideo(s.child("video").value.toString())
                        shot!!.setView((s.child("views").value.toString().toInt()))
                        shot!!.setThumb(s.child("thumb").value.toString())
                        (shotList as ArrayList).add(shot!!)
                        //Collections.reverse(postList)
                        shotvideoListAdapter!!.notifyDataSetChanged()
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}