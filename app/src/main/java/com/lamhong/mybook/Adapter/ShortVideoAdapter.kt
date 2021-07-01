package com.lamhong.mybook.Adapter

import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.lamhong.mybook.Models.ShortVideo
import com.lamhong.mybook.R

class ShortVideoAdapter(options: FirebaseRecyclerOptions<ShortVideo?>)
    : FirebaseRecyclerAdapter<ShortVideo?, ShortVideoAdapter.ViewHolder>(options)
{
    inner class ViewHolder(itemview : View): RecyclerView.ViewHolder(itemview){
        fun setData(obj : ShortVideo){
            videoView.setVideoPath(obj.getVideo())
           // title.text=obj.title
            content.text=obj.getContent().toString()
            videoView.setOnPreparedListener { 
                mp: MediaPlayer? ->  
                progressBar.visibility=View.GONE
                mp?.start()
            }
            videoView.setOnCompletionListener {
                mp ->mp.start()
            }

        }
        lateinit var videoView : VideoView
        lateinit var title : TextView
        lateinit var content: TextView
        lateinit var progressBar : ProgressBar
       // lateinit var fav : ImageView
        var isFav= false

        init {
            videoView= itemview.findViewById(R.id.videoView)
            title= itemview.findViewById(R.id.title)
            content= itemview.findViewById(R.id.content)
            progressBar= itemview.findViewById(R.id.videoProgressBar)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_video_row,
            parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: ShortVideo) {
        holder.setData(model)


    }
}