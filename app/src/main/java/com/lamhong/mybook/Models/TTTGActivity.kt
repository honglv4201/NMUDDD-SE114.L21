package com.lamhong.mybook.Models

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lamhong.mybook.R
import kotlinx.android.synthetic.main.activity_private.*
import kotlinx.android.synthetic.main.activity_private.btn_return_fromsetting
import kotlinx.android.synthetic.main.activity_tttgactivity.*

class TTTGActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tttgactivity)
        btn_return_fromsetting.setOnClickListener {
            finish()
        }
        card1.setOnClickListener {
            startActivity(Intent(this@TTTGActivity, TTCNActivity::class.java))
        }
        card2.setOnClickListener {
            startActivity(Intent(this@TTTGActivity, TKMKActivity::class.java))
        }
        card3.setOnClickListener {
            startActivity(Intent(this@TTTGActivity, BMActivity::class.java))
        }
    }
}