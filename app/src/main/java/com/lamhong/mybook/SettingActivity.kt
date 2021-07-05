package com.lamhong.mybook

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.lamhong.mybook.Models.HelpActivity
import com.lamhong.mybook.Models.RulesActivity
import com.lamhong.mybook.Models.TTTGActivity
import com.lamhong.mybook.Models.ThaydoimatkhauActivity
import kotlinx.android.synthetic.main.activity_forgot_password.*
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        btn_return_fromsetting.setOnClickListener{
            this.finish()
        }
        btnrules.setOnClickListener {
            startActivity(Intent(this@SettingActivity, RulesActivity::class.java))
        }
        btnHelp.setOnClickListener {
            startActivity(Intent(this@SettingActivity, HelpActivity::class.java))
        }
        btnTTTG.setOnClickListener {
            startActivity(Intent(this@SettingActivity, TTTGActivity::class.java))
        }
        btnPassword.setOnClickListener {
            startActivity(Intent(this@SettingActivity, ThaydoimatkhauActivity::class.java))
        }
    }
}