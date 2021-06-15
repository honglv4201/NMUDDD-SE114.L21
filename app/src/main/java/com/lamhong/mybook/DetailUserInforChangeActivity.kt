package com.lamhong.mybook

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.mybook.R
import kotlinx.android.synthetic.main.activity_detail_user_infor_change.*
import kotlinx.android.synthetic.main.activity_detail_user_infor_change.btn_return
import kotlinx.android.synthetic.main.activity_profile_editting.*

class DetailUserInforChangeActivity : AppCompatActivity() {
    private var lstSchool  : ArrayList<String> = ArrayList()
    lateinit var firebaseUser: FirebaseUser
    private var typeChange: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_user_infor_change)
        firebaseUser= FirebaseAuth.getInstance().currentUser
        typeChange= intent.getStringExtra("typechange").toString()
        when(typeChange){
            "school"->{
                img_typeEdit.setImageResource(R.drawable.icon_education_dart)
                tv_typeName.text="Thêm trường học"
            }
            "job" ->{
                img_typeEdit.setImageResource(R.drawable.icon_job)
                tv_typeName.text="Thêm công việc"
            }
            "relationship"->{
                img_typeEdit.setImageResource(R.drawable.icon_relationship_dart)
                tv_typeName.text="Sửa tình trạng cá nhân"
            }
            "home"->{
                img_typeEdit.setImageResource(R.drawable.icon_home_dart)
                tv_typeName.text="Sửa nơi ở"
            }
            "hometown"->{
                img_typeEdit.setImageResource(R.drawable.icon_hometown_dart)
                tv_typeName.text="Sửa quê quán"
            }
            "workplace"->{
                img_typeEdit.setImageResource(R.drawable.icon_workplace_dart)
                tv_typeName.text="Sửa nơi làm việc"
            }
        }
        btn_return.setOnClickListener{
            this.finish()
        }

        getShoolData(typeChange)
        var adapter : ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, lstSchool)

        edit_choose.setAdapter(adapter)
        btn_save_indetail.setOnClickListener{
            saveSchoolData(edit_choose.text.toString(),typeChange)
            saveFunc(typeChange)
            Toast.makeText(this, "Lưu thành công !!", Toast.LENGTH_SHORT).show()
            this.finish()

        }
    }
    fun saveSchoolData(schoolname: String, type: String){
        FirebaseDatabase.getInstance().reference.child("Data").child(type)
            .child(schoolname).setValue(true)
    }

    fun getShoolData(type: String){
        val schooldataRef= FirebaseDatabase.getInstance().reference.child("Data")
            .child(type)
        schooldataRef.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    lstSchool.clear()
                    for (s in snapshot.children){
                        lstSchool.add(s.key.toString())
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    // save
    private fun saveFunc(type : String) {
        if(type=="education" || type == "education1" || type=="education2"){
            FirebaseDatabase.getInstance().reference
                .child("UserDetails").child(firebaseUser.uid!!)
                .child(type).setValue(edit_choose.text.toString())
            FirebaseDatabase.getInstance().reference
                .child("UserDetails").child(firebaseUser.uid!!)
                .child(type).child("status").setValue(edit_choose.text.toString())
        }else{
            FirebaseDatabase.getInstance().reference
                .child("UserDetails").child(firebaseUser.uid!!)
                .child(type).setValue(edit_choose.text.toString())
        }

    }

    fun saveEducation(schoolname: String ){
        val schoolRef= FirebaseDatabase.getInstance().reference.child("UserDetails")
            .child(firebaseUser.uid!!).child("education")
        schoolRef.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){

                }
            }
        })
    }
}