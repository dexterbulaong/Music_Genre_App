package com.example.mysoundclassification

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HoldDown : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hold_down)
        // Declare a button
        val mBtn = findViewById<ImageButton>(R.id.holdButton)

        // implement a setOnLongClickListener to the
        // button that creates a Toast and
        // returns true when actually long-pressed
        mBtn.setOnLongClickListener {
            Toast.makeText(applicationContext, "Button Long Pressed", Toast.LENGTH_SHORT).show()
            true
        }
        // GestureDetecctor to detect long press
        val gestureDetector = GestureDetector(object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {

                // Toast to notify the Long Press
                Toast.makeText(applicationContext, "Long Press Detected", Toast.LENGTH_SHORT).show()
            }
        })

        // onTouchEvent to confirm presence of Touch due to Long Press
        fun onTouchEvent(event: MotionEvent?): Boolean {
            return gestureDetector.onTouchEvent(event)
        }
    }
}
