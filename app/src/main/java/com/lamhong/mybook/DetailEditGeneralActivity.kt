package com.lamhong.mybook

import android.app.ActionBar
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.mybook.Models.UserInfor
import kotlinx.android.synthetic.main.activity_detail_edit_general.*
import kotlinx.android.synthetic.main.fragment_profile.*

class DetailEditGeneralActivity : AppCompatActivity() {
    private var lstEducation  : ArrayList<String> = ArrayList()
    private var lstJob : ArrayList<String> = ArrayList()
    private var lstEducationValue : ArrayList<String> = ArrayList()
    private var edu1 : Boolean = false
    private var edu2 : Boolean = false

    lateinit var firebaseUser: FirebaseUser
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_edit_general)
        firebaseUser= FirebaseAuth.getInstance().currentUser
        btn_return.setOnClickListener{
            this.finish()
        }
        // btn implement function
        btn_addSchool.setOnClickListener{
            if(edu2){
                activityToDetail("education2")
            }
            if(edu1){
                activityToDetail("education1")
            }else{
                activityToDetail("education")
            }

        }
        btn_addHome.setOnClickListener{
            activityToDetail("home")
        }
        btn_addwork.setOnClickListener{
            activityToDetail("job")
        }
        btn_AddHometown.setOnClickListener{
            activityToDetail("homeTown")
        }
        btn_addworkplace.setOnClickListener{
            activityToDetail("workPlace")
        }
        btn_addRelationship.setOnClickListener{
            activityToDetail("relationship")
        }
        getUserDetailInfor()

    }
    private fun getUserDetailInfor(){
        val userDetailRef = FirebaseDatabase.getInstance().reference
            .child("UserDetails").child(firebaseUser.uid!!)
        userDetailRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val userInfor= snapshot.getValue(UserInfor::class.java)
                    userInfor!!.setEducation(snapshot.child("education").value.toString())
                    userInfor!!.setHome(snapshot.child("home").value.toString())
                    userInfor!!.setHomeTown(snapshot.child("homeTown").value.toString())
                    userInfor!!.setJob(snapshot.child("job").value.toString())
                    userInfor!!.setRelationship(snapshot.child("relationship").value.toString())
                    userInfor!!.setWorkPlace(snapshot.child("workPlace").value.toString())

                    if(userInfor.getEducation()=="" || userInfor.getEducation()=="null" ){
                        cv_education.layoutParams.width=ViewGroup.LayoutParams.MATCH_PARENT
                    }
                    else{
                        cv_education.layoutParams.width=222
                        tv_education.text=userInfor.getEducation()
                        edu1 = true
                        if(snapshot.child("education1").value!=null ) {
                            edu2=true
                            tv_education1.text = snapshot.child("education1").value.toString()
                            btn_dahoc1.setText(snapshot.child("education1").child("status").value.toString())
                            if(snapshot.child("education1").child("status").value.toString() == "dahoc"){
                                btn_dahoc1.setTextColor(Color.parseColor("#03A9F4"))
                            }else {
                                btn_dahoc1.setTextColor(Color.parseColor("#F44336"))
                            }

                        }
                        if(snapshot.child("education2").value!=null){

                            tv_education2.text = snapshot.child("education2").value.toString()
                            btn_dahoc2.setText(snapshot.child("education2").child("status").value.toString())
                            if(snapshot.child("education2").child("status").value.toString() == "dahoc"){
                                btn_dahoc2.setTextColor(Color.parseColor("#03A9F4"))
                            }else {
                                btn_dahoc2.setTextColor(Color.parseColor("#F44336"))
                            }

                        }
                        else
                        {

                        }


                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
    private fun activityToDetail(type: String){
        val intent  = Intent(this, DetailUserInforChangeActivity::class.java)
        intent.putExtra("typechange", type)
        startActivity(intent)
    }
}