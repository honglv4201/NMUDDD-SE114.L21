package com.lamhong.mybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.mybook.Models.User
import kotlinx.android.synthetic.main.activity_account_setting.*

class AccountSettingActivity : AppCompatActivity() {
    final lateinit var firebaseUser :FirebaseUser
    private var checker=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_setting)

        btn_logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent= Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        firebaseUser=FirebaseAuth.getInstance().currentUser
        userInfor()
        btn_save.setOnClickListener {
            if(checker=="clicker"){

            }
            else{
                updateUserInfor()

            }
        }


    }

    private fun updateUserInfor() {
        // check data
        when {
            TextUtils.isEmpty(edit_name.text.toString()) -> {
                Toast.makeText(this, "Vui lòng nhập tên ",Toast.LENGTH_LONG)
            }
            TextUtils.isEmpty(edit_mota.text.toString()) -> {
                Toast.makeText(this, "Vui lòng nhập mota ",Toast.LENGTH_LONG)
            }
            else -> {
                val userRef= FirebaseDatabase.getInstance().reference.child("UserInformation")


                val userMap = HashMap<String, Any>()
                userMap["fullname"]= edit_name.text.toString()
                userMap["email"]= edit_mota.text.toString()

                userRef.child(firebaseUser.uid).updateChildren(userMap).addOnCompleteListener{task ->
                    if(task.isSuccessful){

                        val intent= Intent(this@AccountSettingActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        Toast.makeText(this, "Đã cập nhật thông tin !!", Toast.LENGTH_SHORT)
                        startActivity(intent)

                    }else{
                        val failex= task.exception.toString()
                        Toast.makeText(this, "Fail: $failex" , Toast.LENGTH_LONG)

                    }
                }

            }
        }



    }

    private fun userInfor(){
        val userRef= FirebaseDatabase.getInstance().reference.child("UserInformation").child(firebaseUser.uid)
        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user= snapshot.getValue(User::class.java)
                    user?.setName(snapshot.child("fullname").value.toString())
                    edit_name.setText(user?.getName())
                    edit_mota.setText(user?.getEmail())

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }


}
