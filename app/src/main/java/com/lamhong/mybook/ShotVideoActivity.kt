package com.lamhong.mybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.database.FirebaseDatabase
import com.lamhong.mybook.Adapter.ShortVideoAdapter
import com.lamhong.mybook.Models.ShortVideo
import com.lamhong.mybook.R
import kotlinx.android.synthetic.main.activity_shot_video.*
import kotlinx.android.synthetic.main.fragment_short_video.view.*

class ShotVideoActivity : AppCompatActivity() {

    lateinit var adapter: ShortVideoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shot_video)

        val mDatabase = FirebaseDatabase.getInstance().reference.child("ShotVideos")
                .orderByChild("views").limitToLast(5)


        val options = FirebaseRecyclerOptions.Builder<ShortVideo>()
            .setQuery(mDatabase, ShortVideo::class.java).build()

        adapter= ShortVideoAdapter(this,options)



        vpaper.adapter=adapter
        vpaper.setCurrentItem(3,false)
        // set full
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_FULLSCREEN,
//            WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
    override fun onStart() {
        super.onStart()
        adapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter.stopListening()
    }
}