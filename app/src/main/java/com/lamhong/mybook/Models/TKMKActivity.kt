package com.lamhong.mybook.Models

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lamhong.mybook.R
import kotlinx.android.synthetic.main.activity_setting.*

class TKMKActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tkmkactivity)
        btn_return_fromsetting.setOnClickListener{
            this.finish()
        }
    }
}