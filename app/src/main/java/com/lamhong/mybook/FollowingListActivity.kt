package com.lamhong.mybook

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.lamhong.mybook.Adapter.FriendAdapter
import io.grpc.internal.JsonUtil.getList
import kotlinx.android.synthetic.main.activity_following_list.*
import kotlinx.android.synthetic.main.activity_following_list.btn_tatca_friendlist
import kotlinx.android.synthetic.main.activity_following_list.recycleView_friendList
import kotlinx.android.synthetic.main.activity_friend_list.*
import kotlinx.android.synthetic.main.sheet_layout_profile.*

class FollowingListActivity : AppCompatActivity() {
    private var userID: String = ""
    private var type: String = ""
    private var lstFollowList : ArrayList<String> = ArrayList()
    private var lstFollowmeList : ArrayList<String> = ArrayList()
    private var lstBlockList : ArrayList<String> = ArrayList()

    private var lstFollowLists : ArrayList<String> = ArrayList()
    private var lstFollowmeLists : ArrayList<String> = ArrayList()
    private var lstBlockLists : ArrayList<String> = ArrayList()

    private var lstFollowListAdapter : FriendAdapter?=null
    private var lstFollowmeListAdapter : FriendAdapter?=null
    private var lstBlockListAdapter : FriendAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following_list)
        btn_return_fromfollowlist.setOnClickListener{
            this.finish()
        }
        //innit something
        userID= intent.getStringExtra("userID").toString()
        type= intent.getStringExtra("type").toString()

        if(FirebaseAuth.getInstance().currentUser.uid != userID){
            if(btn_dachan!=null)
                btn_dachan.visibility= View.GONE
        }

        // list
        getList()
        var recyclerView = recycleView_friendList
        var gridLayoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager= gridLayoutManager

        lstFollowmeListAdapter= this?.let { FriendAdapter(it, lstFollowmeList as ArrayList, lstFollowmeLists as ArrayList,userID  ) }
        lstFollowListAdapter= this?.let { FriendAdapter(it, lstFollowList as ArrayList, lstFollowLists as ArrayList,userID  ) }
        lstBlockListAdapter= this?.let { FriendAdapter(it, lstBlockList as ArrayList, lstBlockLists as ArrayList,userID  ) }


        // set if user choosen
        if(type=="follower"){
            setBtnAppearanceSelected(btn_nguoitheodoi)
            setBtnAppearanceNonSelected(btn_dangtheodoi)
            setBtnAppearanceNonSelected(btn_dachan)
            //recyclerView.layoutManager= linearLayoutManager
            //recyclerView.adapter= lst_confirmFriendAdapter
            // recyclerView1.visibility= View.GONE
            recyclerView.adapter= lstFollowmeListAdapter
        }
        else {
            setBtnAppearanceNonSelected(btn_dachan)
            setBtnAppearanceNonSelected(btn_nguoitheodoi)
            setBtnAppearanceSelected(btn_dangtheodoi)
            //recyclerView.layoutManager= gridLayoutManager
            //recyclerView.adapter= lst_trueFriendAdapter
            //recyclerView1.visibility= View.GONE
            recyclerView.adapter= lstFollowListAdapter
        }


        //set button selected click
        btn_dangtheodoi.setOnClickListener{
            setBtnAppearanceSelected(btn_dangtheodoi)
            setBtnAppearanceNonSelected(btn_nguoitheodoi)
            setBtnAppearanceNonSelected(btn_dachan)
            //recyclerView.layoutManager= gridLayoutManager
            //recyclerView.adapter= lst_trueFriendAdapter
            //recyclerView1.visibility= View.GONE
            recyclerView.adapter= lstFollowListAdapter

        }
        btn_nguoitheodoi.setOnClickListener{
            setBtnAppearanceSelected(btn_nguoitheodoi)
            setBtnAppearanceNonSelected(btn_dangtheodoi)
            setBtnAppearanceNonSelected(btn_dachan)
            //recyclerView.layoutManager= linearLayoutManager
            //recyclerView.adapter= lst_waittingFriendAdapter
            // recyclerView1.visibility= View.GONE
            recyclerView.adapter= lstFollowmeListAdapter

        }

        btn_dachan.setOnClickListener{
            setBtnAppearanceSelected(btn_dachan)
            setBtnAppearanceNonSelected(btn_dangtheodoi)
            setBtnAppearanceNonSelected(btn_nguoitheodoi)
            //recyclerView.layoutManager= linearLayoutManager
            //recyclerView.adapter= lst_confirmFriendAdapter
            // recyclerView1.visibility= View.GONE
            recyclerView.adapter= lstBlockListAdapter
        }

    }
    fun getList(){
        // dealine nên tạm tên nhá =))
        lstFollowList= ArrayList()
        val friendRef= FirebaseDatabase.getInstance().reference
            .child("Friends").child(userID).child("followingList")
        friendRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (lstFollowList as ArrayList).clear()
                    for (ss in snapshot.children){
                        (lstFollowList as ArrayList).add(ss.key.toString())
                        (lstFollowLists as ArrayList).add("friend")
                    }
                    lstFollowListAdapter!!.notifyDataSetChanged()
                }
            }
        })

        lstFollowmeList= ArrayList()
        val friendRef1= FirebaseDatabase.getInstance().reference
            .child("Friends").child(userID).child("followerList")
        friendRef1.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (lstFollowmeList as ArrayList).clear()
                    for (ss in snapshot.children){
                        (lstFollowmeList as ArrayList).add(ss.key.toString())
                        (lstFollowmeLists as ArrayList).add("friend")
                    }
                    lstFollowmeListAdapter!!.notifyDataSetChanged()
                }
            }
        })

        lstBlockList= ArrayList()
        val friendRef2= FirebaseDatabase.getInstance().reference
            .child("Friends").child(userID).child("blockList")
        friendRef2.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    (lstBlockList as ArrayList).clear()
                    for (ss in snapshot.children){
                        (lstBlockList as ArrayList).add(ss.key.toString())
                        (lstBlockLists as ArrayList).add("friend")
                    }
                    lstBlockListAdapter!!.notifyDataSetChanged()
                }
            }
        })

    }
    fun setBtnAppearanceNonSelected(btn : AppCompatButton){
        btn.setTextColor(Color.parseColor("#989898"))
        btn.setBackgroundColor(Color.parseColor("#FFFFFF"))
    }
    fun setBtnAppearanceSelected(btn : AppCompatButton){

        btn.setTextColor(Color.parseColor("#FFFFFF"))
        btn.setBackgroundColor(Color.parseColor("#00BCD4"))
    }
}