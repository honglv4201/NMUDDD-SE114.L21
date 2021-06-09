package com.lamhong.mybook.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lamhong.mybook.Models.User
import com.lamhong.mybook.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.message_users_item.view.*

class MessageUsersAdapter(private val messageUsersList: List<User>) : RecyclerView.Adapter<MessageUsersAdapter.MessageUsersViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageUsersViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.message_users_item,parent,false)

        return MessageUsersViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return messageUsersList.size
    }

    override fun onBindViewHolder(holder: MessageUsersViewHolder, position: Int) {
        val currentItem = messageUsersList[position]

        holder.textView.text = currentItem.getName()
        Picasso.get().load(currentItem?.getAvatar()).placeholder(R.drawable.loading_image).into(holder.imageView)

    }

    class MessageUsersViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.profile_image_message
        val textView: TextView = itemView.user_name_message
    }
}