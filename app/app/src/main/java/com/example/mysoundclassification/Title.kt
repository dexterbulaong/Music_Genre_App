package com.example.mysoundclassification

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Title : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        val toGenre = findViewById<Button>(R.id.button1)
        toGenre.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val toReal = findViewById<Button>(R.id.button2)
        toReal.setOnClickListener {
            val intent = Intent(this, HoldDown::class.java)
            startActivity(intent)
        }
    }
}