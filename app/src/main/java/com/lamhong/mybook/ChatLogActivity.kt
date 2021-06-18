package com.lamhong.mybook

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.lamhong.mybook.Adapter.MessageAdapter
import com.lamhong.mybook.Models.Message
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.util.*
import kotlin.collections.ArrayList


class ChatLogActivity : AppCompatActivity() {

    private var senderRoom:String ?= null
    private var receiveRoom:String ?= null

    var seenListener: ValueEventListener? = null
    var userRefForSeen: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)


        var name:String? = intent.getStringExtra("name")
        var image:String? = intent.getStringExtra("image")
        var receiverUid:String?= intent.getStringExtra("uid")

        setSupportActionBar(toolbar_chatlog)

        user_name_chat.text = name
        Picasso.get().load(image).into(profile_image_chat)

        //supportActionBar?.title = name
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var senderUid: String? = FirebaseAuth.getInstance().uid

        senderRoom = senderUid + receiverUid
        receiveRoom = receiverUid + senderUid

        val messages = ArrayList<Message>()

        val adater:MessageAdapter = MessageAdapter(messages, senderRoom!!, receiveRoom!!)

        rv_chat_log.layoutManager = LinearLayoutManager(this)
        rv_chat_log.adapter = adater

        FirebaseDatabase.getInstance().reference
            .child("chats")
            .child(senderRoom.toString())
            .child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    messages.clear()
                    for(ss in snapshot.children) {
                        val message = ss.getValue(Message::class.java)
                        message!!.setisSeen(ss.child("seen").value as Boolean)
                        if (message != null) {
                            messages.add(message)
                        }
                    }

                    adater.notifyDataSetChanged()
                    rv_chat_log.smoothScrollToPosition(adater.itemCount)
                }
            })



        btn_send_message.setOnClickListener {

            val messageTxt: String = messagebox.text.toString()

            if (!TextUtils.isEmpty(messageTxt)) {


                val date = Date()


                val message: com.lamhong.mybook.Models.Message = com.lamhong.mybook.Models.Message(messageTxt,
                        senderUid.toString(), date.time.toString(), false)

                messagebox.text.clear()

                val lastMess = hashMapOf<String, Any?>()

                lastMess.put("lastMess", message.getMessage())
                lastMess.put("lastTime", date.time)

                FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                FirebaseDatabase.getInstance().reference
                        .child("chats")
                        .child(senderRoom.toString())
                        .child("message")
                        .push()
                        .setValue(message).addOnSuccessListener {
                            FirebaseDatabase.getInstance().reference
                                    .child("chats")
                                    .child(receiveRoom.toString())
                                    .child("message")
                                    .push()
                                    .setValue(message).addOnSuccessListener {

                                    }

                            val lastMess = hashMapOf<String, Any?>()

                            lastMess.put("lastMess", message.getMessage())
                            lastMess.put("lastTime", date.time)

                            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                            FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                            //rv_chat_log.smoothScrollToPosition(adater.itemCount)

                        }
            }
        }

        attachment.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent,25)
        }

        //Seen messgae



        userRefForSeen = FirebaseDatabase.getInstance().reference
                .child("chats")
                .child(receiveRoom.toString())
                .child("message")

        seenListener = userRefForSeen!!.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (ss in snapshot.children) {
                    val chat = ss.getValue(Message::class.java)

                    if (chat!=null) {
                        val seen = hashMapOf<String, Any?>()
                        seen.put("seen",true)
                        ss.ref.updateChildren(seen)
                    }

                }
            }

        })


/*        FirebaseDatabase.getInstance().reference.child("presence").child(receiveRoom.toString()).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val status = snapshot.getValue(String::class.java)
                    if (status != null) {
                        if (!status.isEmpty()) {
                            status_chat.text = status
                            status_chat.visibility = View.VISIBLE

                        }
                    }
                }
            }

        })*/







    }

/*    override fun onResume() {
        super.onResume()
        val currentuid = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().reference.child("presence").child(currentuid.toString()).setValue("Online")

    }

    override fun onStop() {

        val currentuid = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().reference.child("presence").child(currentuid.toString()).setValue("Offline")
        super.onStop()
    }*/

    override fun onPause() {
        super.onPause()

        seenListener?.let { userRefForSeen?.removeEventListener(it) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var senderUid: String? = FirebaseAuth.getInstance().uid

        val dialog = ProgressDialog(this)
        dialog.setMessage("Uploading image...")
        dialog.setCancelable(false)

        if (requestCode == 25) {
            if (data!=null) {
                if (data.data != null) {
                    val selectedImage = data.data
                    val calendar = Calendar.getInstance()
                    val reference = FirebaseStorage.getInstance().reference.child("chats").child(calendar.timeInMillis.toString() + "")
                    dialog.show()
                    reference.putFile(selectedImage as Uri).addOnCompleteListener {
                        dialog.dismiss()
                        if (it.isSuccessful) {
                            reference.downloadUrl.addOnSuccessListener {
                                val filePath = it.toString()

                                val date = Date()
                                val messageTxt:String = messagebox.text.toString()

                                val message:com.lamhong.mybook.Models.Message = com.lamhong.mybook.Models.Message(messageTxt,
                                        senderUid.toString(),date.time.toString(),false)

                                message.setMessage("[Photo]")

                                message.setImageUrl(filePath)

                                messagebox.text.clear()

                                val lastMess = hashMapOf<String, Any?>()

                                lastMess.put("lastMess",message.getMessage())
                                lastMess.put("lastTime",date.time)

                                FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                                FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                                FirebaseDatabase.getInstance().reference
                                        .child("chats")
                                        .child(senderRoom.toString())
                                        .child("message")
                                        .push()
                                        .setValue(message).addOnSuccessListener {
                                            FirebaseDatabase.getInstance().reference
                                                    .child("chats")
                                                    .child(receiveRoom.toString())
                                                    .child("message")
                                                    .push()
                                                    .setValue(message).addOnSuccessListener {

                                                    }

                                            val lastMess = hashMapOf<String, Any?>()

                                            lastMess.put("lastMess",message.getMessage())
                                            lastMess.put("lastTime",date.time)

                                            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                                            FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                                            //rv_chat_log.smoothScrollToPosition(adater.itemCount)

                                        }

                                //Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }


                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater:MenuInflater = menuInflater
        inflater.inflate(R.menu.top_menu_chat,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.chat_color -> {}
            R.id.chat_call -> {}
            R.id.chat_videocall -> {}
            R.id.chat_see_profile -> {}
            R.id.chat_nickname -> {}
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}