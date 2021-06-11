package com.lamhong.mybook.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.lamhong.mybook.Models.Message
import com.lamhong.mybook.R
import kotlinx.android.synthetic.main.item_sent_message.view.*

class MessageAdapter(private val messageList: ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder> () {

    companion object {
        const val ITEM_SENT = 1
        const val ITEM_RECEIVE = 2
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM_SENT) {
            return SentViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_sent_message,parent,false))
        }
        return ReceiveViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_receive_message,parent,false))
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == ITEM_SENT) {
            (holder as SentViewHolder).bind(position)

        }
        else {
            (holder as ReceiveViewHolder).bind(position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (FirebaseAuth.getInstance().uid.equals(messageList[position].getSenderID())){
            return ITEM_SENT
        }
        else {
            return ITEM_RECEIVE
        }
    }


    private inner class SentViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var message = itemView.message
        fun bind(position: Int){
            val recyclerViewModel = messageList[position]
            message.text= recyclerViewModel.getMessage()
        }

    }

    private inner class ReceiveViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView){
        var message = itemView.message
        fun bind(position: Int){
            val recyclerViewModel = messageList[position]
            message.text= recyclerViewModel.getMessage()
        }
    }



}