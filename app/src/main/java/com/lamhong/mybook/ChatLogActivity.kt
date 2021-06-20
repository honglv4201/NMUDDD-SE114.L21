package com.lamhong.mybook

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.lamhong.mybook.Adapter.MessageAdapter
import com.lamhong.mybook.Models.Message
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.emoji.EmojiTextView
import kotlinx.android.synthetic.main.activity_chat_log.*
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class ChatLogActivity : AppCompatActivity() {

    private var senderRoom:String ?= null
    private var receiveRoom:String ?= null

    var seenListener: ValueEventListener? = null
    var userRefForSeen: DatabaseReference? = null

    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 200

    private val IMAGE_PICK_CAMERA_CODE = 300
    private val IMAGE_PICK_GALLERY_CODE = 400

    var cameraPermissions:Array<String> = emptyArray()
    var storagePermissions:Array<String> = emptyArray()

    var imageUri:Uri? = null

    var senderUid: String? = FirebaseAuth.getInstance().uid



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)


        cameraPermissions = arrayOf(android.Manifest.permission.CAMERA,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)


        var name:String? = intent.getStringExtra("name")
        var image:String? = intent.getStringExtra("image")
        var receiverUid:String?= intent.getStringExtra("uid")

        setSupportActionBar(toolbar_chatlog)

        user_name_chat.text = name
        Picasso.get().load(image).into(profile_image_chat)

        //supportActionBar?.title = name
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



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

        messagebox.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (TextUtils.isEmpty(messagebox.text.toString())) {
                    btn_send_message.setImageDrawable(resources.getDrawable(R.drawable.ic_like))
                }
                else {
                    btn_send_message.setImageDrawable(resources.getDrawable(R.drawable.ic_send_green))
                }
            }

        })

        // emoji

        val popup = EmojiPopup.Builder.fromRootView(
                findViewById(R.id.root_view)
        ).build(messagebox)

        btn_icon_chat.setOnClickListener{
            popup.toggle()
        }



        btn_send_message.setOnClickListener {

            val emojiTextVie = LayoutInflater.from(this).inflate(R.layout.emoji_text_view,linearLayoutEmoji,false) as EmojiTextView

            emojiTextVie.setText(messagebox.text.toString())

            linearLayoutEmoji.addView(emojiTextVie)



            val messageTxt: String = messagebox.text.toString()

            if (!TextUtils.isEmpty(messageTxt)) {


                val date = Date()


                val message: com.lamhong.mybook.Models.Message = com.lamhong.mybook.Models.Message(messageTxt,
                        senderUid.toString(), date.time.toString(), false,"text")

                messagebox.text.clear()

                val lastMess = hashMapOf<String, Any?>()

                lastMess.put("lastMess", message.getMessage())
                lastMess.put("lastTime", date.time.toString())

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
                            lastMess.put("lastTime", date.time.toString())

                            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                            FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                            //rv_chat_log.smoothScrollToPosition(adater.itemCount)

                        }
            }
        }

//        attachment.setOnClickListener {
//            val intent = Intent()
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "image/*"
//            startActivityForResult(intent,25)
//        }


        attachment.setOnClickListener{
            if (!checkStoragePermission()) {
                requestStoragePermission()
            }
            else {
                pickFromGallery()
            }
        }

        camera.setOnClickListener{
//            if (!checkCameraPermission()) {
//                requestCameraPermission()
//            }
//            else {
//                pickFromCamera()
//            }
            pickFromCamera()
        }


        //Seen message

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


    private fun checkStoragePermission() : Boolean {
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        return result
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE)
    }

    private fun checkCameraPermission() : Boolean {
        val result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED)
        val result1 = ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
        return result && result1
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE)
    }

    private fun pickFromCamera() {
        val cv = ContentValues()
        cv.put(MediaStore.Images.Media.TITLE,"Temp Pick")
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Temp Descr")
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE)

    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE)

    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted) {
                        pickFromCamera()
                    }
                    else {
                        Toast.makeText(this,"Camera & Storage both permissions are necessary...",Toast.LENGTH_LONG).show()
                    }
                }
                else {

                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted) {
                        pickFromGallery()
                    }
                    else {
                        Toast.makeText(this,"Storage permissions necessary...",Toast.LENGTH_LONG).show()
                    }
                }
                else {

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGE_PICK_GALLERY_CODE) {
                imageUri = data?.data
                sendImageMess(imageUri)
            }
            if (requestCode == IMAGE_PICK_CAMERA_CODE) {
                sendImageMess(imageUri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendImageMess(imageUri: Uri?) {

        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Sending image...")
        progressDialog.show()

        val timestamp = "" + System.currentTimeMillis()
        val fileNameAndPath = "ChatImages/" + "mess_" + timestamp


        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100,baos)
        val data = baos.toByteArray()
        val ref = FirebaseStorage.getInstance().reference.child(fileNameAndPath)
        ref.putBytes(data)
                .addOnSuccessListener {
                    progressDialog.dismiss()
                    val uriTask = it.storage.downloadUrl
                    while (!uriTask.isSuccessful){}
                    val dowloadUri = uriTask.result.toString()

                    if (uriTask.isSuccessful) {

                        val message: com.lamhong.mybook.Models.Message = com.lamhong.mybook.Models.Message(dowloadUri,
                                senderUid.toString(), timestamp, false,"image")


                        val lastMess = hashMapOf<String, Any?>()

                        lastMess["lastMess"] = "[Photo]"
                        lastMess["lastTime"] = timestamp

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

                                    lastMess["lastMess"] = "[Photo]"
                                    lastMess["lastTime"] = timestamp


                                    FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
                                    FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)

                                    //rv_chat_log.smoothScrollToPosition(adater.itemCount)

                                }




                    }
                }
                .addOnFailureListener{
                    progressDialog.dismiss()
                }
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

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        var senderUid: String? = FirebaseAuth.getInstance().uid
//
//        val dialog = ProgressDialog(this)
//        dialog.setMessage("Uploading image...")
//        dialog.setCancelable(false)
//
//        if (requestCode == 25) {
//            if (data!=null) {
//                if (data.data != null) {
//                    val selectedImage = data.data
//                    val calendar = Calendar.getInstance()
//                    val reference = FirebaseStorage.getInstance().reference.child("chats").child(calendar.timeInMillis.toString() + "")
//                    dialog.show()
//                    reference.putFile(selectedImage as Uri).addOnCompleteListener {
//                        dialog.dismiss()
//                        if (it.isSuccessful) {
//                            reference.downloadUrl.addOnSuccessListener {
//                                val filePath = it.toString()
//
//                                val date = Date()
//                                val messageTxt:String = messagebox.text.toString()
//
//                                val message:com.lamhong.mybook.Models.Message = com.lamhong.mybook.Models.Message(messageTxt,
//                                        senderUid.toString(),date.time.toString(),false,"")
//
//                                message.setMessage("[Photo]")
//
//                                message.setImageUrl(filePath)
//
//                                messagebox.text.clear()
//
//                                val lastMess = hashMapOf<String, Any?>()
//
//                                lastMess.put("lastMess",message.getMessage())
//                                lastMess.put("lastTime",date.time)
//
//                                FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
//                                FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)
//
//                                FirebaseDatabase.getInstance().reference
//                                        .child("chats")
//                                        .child(senderRoom.toString())
//                                        .child("message")
//                                        .push()
//                                        .setValue(message).addOnSuccessListener {
//                                            FirebaseDatabase.getInstance().reference
//                                                    .child("chats")
//                                                    .child(receiveRoom.toString())
//                                                    .child("message")
//                                                    .push()
//                                                    .setValue(message).addOnSuccessListener {
//
//                                                    }
//
//                                            val lastMess = hashMapOf<String, Any?>()
//
//                                            lastMess.put("lastMess",message.getMessage())
//                                            lastMess.put("lastTime",date.time)
//
//                                            FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom.toString()).updateChildren(lastMess)
//                                            FirebaseDatabase.getInstance().reference.child("chats").child(receiveRoom.toString()).updateChildren(lastMess)
//
//                                            //rv_chat_log.smoothScrollToPosition(adater.itemCount)
//
//                                        }
//
//                                //Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//
//
//                }
//            }
//        }
//    }

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