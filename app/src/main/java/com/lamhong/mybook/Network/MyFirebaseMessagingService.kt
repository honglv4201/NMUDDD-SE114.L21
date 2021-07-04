package com.lamhong.mybook.Network

import android.content.Intent
import android.util.Log
import androidx.annotation.NonNull
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.core.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.lamhong.mybook.IncomingInvitationActivity
import com.lamhong.mybook.Models.User
import com.lamhong.mybook.Utilities.Constants


class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(@NonNull s: String) {
        super.onNewToken(s)
        Log.d("TAG", "Token " + s)
        sendFCMTokenToDatabase(s)
    }

    override fun onMessageReceived(@NonNull remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("TAG", "RECEIVED")
        val intent = Intent(applicationContext, IncomingInvitationActivity ::class.java )

        val user : User = User()
        val type = remoteMessage.data.get(Constants.REMOTE_MSG_TYPE)
        if(type!=null){
            if(type.equals(Constants.REMOTE_MSG_INVITATION)){
                user.setUid(remoteMessage.data.get(Constants.KEY_UID)!!)
                user.setEmail(remoteMessage.data.get(Constants.KEY_EMAIL)!!)
                user.setName(remoteMessage.data.get(Constants.KEY_FULLNAME)!!)
                user.setAvatar(remoteMessage.data.get(Constants.KEY_AVATAR)!!)
                user.setToken(remoteMessage.data.get(Constants.KEY_FCM_TOKEN)!!)

                intent.putExtra("UserInfor", user)
                intent.putExtra(Constants.REMOTE_MSG_MEETING_TYPE, remoteMessage.data.get(Constants.REMOTE_MSG_MEETING_TYPE))
                intent.putExtra(Constants.REMOTE_MSG_MEETING_ROOM, remoteMessage.data.get(Constants.REMOTE_MSG_MEETING_ROOM))
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                val tmpIntent = Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                tmpIntent.putExtra(
                    Constants.REMOTE_MSG_INVITATION_RESPONSE,
                    remoteMessage.data.get(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                )
                LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(tmpIntent)
            }
        }

    }

    var meUid : String? = FirebaseAuth.getInstance().uid
    private fun sendFCMTokenToDatabase(token : String){
        FirebaseDatabase.getInstance().reference.child("UserInformation")
            .child(meUid.toString())
            .child(Constants.KEY_FCM_TOKEN)
            .setValue(token)

    }
}