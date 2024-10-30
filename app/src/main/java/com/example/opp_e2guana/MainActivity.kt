package com.example.opp_e2guana

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.page1)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //var buttonState: Boolean = false
        var text1: TextView = findViewById(R.id.textView)
        val testButton: Button = findViewById(R.id.JUNG_testbutton)

        testButton.setOnClickListener {
            text1.text = "버튼 확인"
        }
//        if(buttonState) {
//            text1.text = "텍스트 true"
//        } else {
//            text1.text = "텍스트 false"
//        }

    }
}