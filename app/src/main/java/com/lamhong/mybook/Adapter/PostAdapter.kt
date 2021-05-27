package com.lamhong.mybook.Adapter

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.mybook.Models.Post
import com.lamhong.mybook.Models.User
import com.lamhong.mybook.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class PostAdapter (private val mcontext: Context, private val mPost : List<Post>): RecyclerView.Adapter<PostAdapter.ViewHolder>(){

    private var firebaseUser : FirebaseUser?=null


    inner class ViewHolder(@NonNull itemVIew: View): RecyclerView.ViewHolder(itemVIew){
//        var postImage :ImageView
//        var profileImage : CircleImageView= itemView.findViewById(R.id.user_profile_image_search)
//        var likeButton : ImageView= itemView.findViewById(R.id.post_image_like_btn)
//        var commentButton: ImageView= itemView.findViewById(R.id.post_image_comment_btn)
//
//        var userName: TextView = itemView.findViewById(R.id.user_name_search)
//        var publisher : TextView = itemView.findViewById(R.id.publisher)
        var postImage :ImageView
        var profileImage : CircleImageView
        var likeButton : ImageView
        var commentButton: ImageView

        var userName: TextView
        var publisher : TextView

        init {
            postImage = itemView.findViewById(R.id.post_image_home)
            profileImage = itemView.findViewById(R.id.user_profile_image_search)
            likeButton = itemView.findViewById(R.id.post_image_like_btn)
            commentButton = itemView.findViewById(R.id.post_image_comment_btn)

            userName = itemView.findViewById(R.id.user_name_search)
            publisher = itemView.findViewById(R.id.publisher)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mcontext).inflate(R.layout.posts_layout, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser= FirebaseAuth.getInstance().currentUser

        val post= mPost[position]
        Picasso.get().load(post.getpost_image()).into(holder.postImage)

        publishInfo(holder.profileImage, holder.userName, holder.publisher, post.getpublisher())
    }



    override fun getItemCount(): Int {
        return mPost.size
    }


    private fun publishInfo(profileImage: CircleImageView, userName: TextView, publisher: TextView, publiser: String) {
        val userRef= FirebaseDatabase.getInstance().reference.child("UserInformation").child(publiser)

        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue<User>(User::class.java)
                    Picasso.get().load(user!!.getAvatar()).placeholder(R.drawable.duongtu).into(profileImage)
                    publisher.setText(user!!.getName())
                    userName.setText(user!!.getName())
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}