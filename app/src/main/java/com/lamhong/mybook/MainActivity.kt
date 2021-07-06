package com.lamhong.mybook

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.lamhong.mybook.Fragment.SettingFragment
import com.lamhong.mybook.Fragment.*

class MainActivity : AppCompatActivity() {
//    private lateinit var textview: TextView
 //   internal var selectedFragment: Fragment ?=null
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
                moveFragment(zHome())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {

               moveFragment(MessageFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                //item.isChecked=false
               // startActivity(Intent(this, Post_Activity::class.java))
              //  moveFragment(ShortVideoFragment())
                moveFragment(VideoFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
                moveFragment(NotifyFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
                moveFragment(SettingFragment())
                return@OnNavigationItemSelectedListener true
            }

        }


        false
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        moveFragment(zHome())
    }

    private fun moveFragment(fragment :Fragment){
        val fragmentselect = supportFragmentManager.beginTransaction()
        fragmentselect.replace(R.id.frameLayout, fragment)
        fragmentselect.commit()
    }
    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this)
        alert.setTitle("Xác nhận")
        alert.setIcon(R.drawable.ic_exit)
        alert.setMessage("Bạn muốn thoát khỏi ứng dụng ?")
        alert.setCancelable(false)
        alert.setNegativeButton("Thoát") { dialog, which -> finish() }
        alert.setPositiveButton("Không") { dialog, which -> }
        val alertDialog = alert.create()
        alertDialog.show()
    }
}