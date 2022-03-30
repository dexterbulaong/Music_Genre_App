package com.example.mysoundclassification

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class Title : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var modelPath = "genre_classifier_model.tflite"
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)

        val MyVersion = Build.VERSION.SDK_INT
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission()
            }
        }

        val toGenre = findViewById<Button>(R.id.button1)
        toGenre.setOnClickListener {
            val intent = Intent(this, HoldDown::class.java)
            startActivity(intent)
        }

        val toReal = findViewById<Button>(R.id.button2)
        toReal.setOnClickListener {
            val intent = Intent(this, AnalyzeActivity::class.java)
            startActivity(intent)
        }


    }

    private fun checkIfAlreadyhavePermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS)
        return result == PackageManager.PERMISSION_GRANTED
    }

    private fun requestForSpecificPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.RECORD_AUDIO
            ),
            101
        )
    }
}