package com.lamhong.mybook.Models

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.lamhong.mybook.R
import kotlinx.android.synthetic.main.activity_rules.*

class RulesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        btn_return_fromsetting.setOnClickListener {
            finish()
        }
    }
}