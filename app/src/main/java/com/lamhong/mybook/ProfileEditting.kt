package com.lamhong.mybook

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.lamhong.mybook.Models.UserInfor
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_profile_editting.*

class ProfileEditting : AppCompatActivity() {
    lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_editting)

        //init firebase
        firebaseUser= FirebaseAuth.getInstance().currentUser

        btn_return.setOnClickListener{
            finish()
        }

        circleImageAvatar.setOnClickListener{
            startActivity(Intent(this, ChangeAvatarActivity::class.java))
        }
        anhbia.setOnClickListener{
            startActivity(Intent(this, ChangeCoverImageActivity::class.java))
        }
        setAccountInfor()

        // bio changing
        btn_saveBio.setOnClickListener{
            if(edit_bio.text.length<1 ){
                Toast.makeText(this, "Vui lòng nhập nội dung trước !!", Toast.LENGTH_SHORT).show()
            }
            else{
                saveBio()
                edit_bio.clearFocus()
                val imm = this?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.toggleSoftInput(InputMethodManager.RESULT_HIDDEN, 0)
                Toast.makeText(this, "Đã lưu thành công! ", Toast.LENGTH_SHORT).show()

            }
        }

        // detail infor changing
        change_detail.setOnClickListener{
            startActivity(Intent(this, DetailEditGeneralActivity::class.java))
        }
        tv_education.setOnClickListener{
            activityToDetail("education")
        }
        tv_job.setOnClickListener{
            activityToDetail("job")
        }
        tv_home.setOnClickListener{
            activityToDetail("home")
        }
        tv_hometown.setOnClickListener{
            activityToDetail("homeTown")
        }
        tv_relationship.setOnClickListener{
            activityToDetail("relationship")
        }
        tv_workplace.setOnClickListener{
            activityToDetail("workPlace")
        }
    }
    fun activityToDetail(type:String){
        val detailIntent = Intent(this, DetailUserInforChangeActivity::class.java)
        detailIntent.putExtra("typechange", type)
        startActivity(detailIntent)
    }

    private fun saveBio() {
        FirebaseDatabase.getInstance().reference
            .child("UserDetails").child(firebaseUser.uid!!)
            .child("bio").setValue(edit_bio.text.toString())

    }

    fun setAccountInfor(){
        val userRef= FirebaseDatabase.getInstance().reference
            .child("UserInformation").child(firebaseUser.uid!!)
        userRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Picasso.get().load(snapshot.child("avatar").value.toString()).placeholder(R.drawable.cty)
                        .into(circleImageAvatar)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        val userInforRef= FirebaseDatabase.getInstance().reference
            .child("UserDetails")
            .child(firebaseUser.uid!!)
        userInforRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val user = snapshot.getValue(UserInfor::class.java)
                    user!!.setBio(snapshot.child("bio").value.toString())

                    Picasso.get().load(snapshot.child("coverImage").value.toString()).placeholder(R.drawable.cty)
                        .into(anhbia)
                    edit_bio.setText(user!!.getBio())
                }
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

    }
}