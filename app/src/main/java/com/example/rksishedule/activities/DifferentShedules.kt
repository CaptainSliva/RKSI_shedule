package com.example.rksishedule.activities

import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.rksishedule.R

class DifferentShedules: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_different_shedules)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_different_shedules)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bBack = findViewById<ImageButton>(R.id.b_back)

        bBack.setOnClickListener {
            finish()
        }



    }
}