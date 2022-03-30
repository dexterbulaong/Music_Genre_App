package com.example.mysoundclassification

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate


class HoldDown : AppCompatActivity(){

    // path for our model
    var modelPath = "genre_classifier_model.tflite"

    var probabilityThreshold: Float = 0.3f

    lateinit var textView: TextView
    lateinit var textAnswer: TextView


    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hold_down)
        // Declare a button
        val mBtn = findViewById<ImageButton>(R.id.holdButton)
        textView = findViewById<TextView>(R.id.holdText)
        textAnswer = findViewById<TextView>(R.id.textAnswer)
        var textAgain = findViewById<TextView>(R.id.tryAgain)


        val classifier = AudioClassifier.createFromFile(this, modelPath)
        val tensor = classifier.createInputTensorAudio()
        val format = classifier.requiredTensorAudioFormat
        val record = classifier.createAudioRecord()
        var storedStr = ""
        var timer = Timer()

        val listGenre = mutableListOf<String>()
        val listProb = mutableListOf<Float>()

        // button now recognizes being held and released instead of just activating via long press
        mBtn.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    //Toast.makeText(applicationContext, "Button Held", Toast.LENGTH_SHORT).show()
                    listGenre.clear()
                    listProb.clear()
                    timer = Timer()
                    ///////////////////////////////////////////////////////////////////////

                    timer.scheduleAtFixedRate(1, 1000) {
                        record.startRecording()
                        val numberOfSamples = tensor.load(record)
                        val output = classifier.classify(tensor)

                        // Check if music is playing
                        var filteredModelOutput = output[0].categories.filter {
                            it.label.contains("Music") && it.score > probabilityThreshold
                        }

                        // Given there's music, what genre is it?
                        if (filteredModelOutput.isNotEmpty()) {
                            Log.i("Yamnet", "Song detected!")
                            filteredModelOutput = output[1].categories.filter {
                                it.score > probabilityThreshold
                            }
                        }

                        output.clear()

                        val outputStr =
                            filteredModelOutput.sortedBy { -it.score }
                                .joinToString(separator = "\n") { "${it.label} -> ${it.score} " }

                        if (outputStr.isNotEmpty())
                            runOnUiThread {

                                val label =
                                    filteredModelOutput.sortedBy { -it.score }[0].label.toString()
                                listGenre.add(label)

//                                val score =
//                                    filteredModelOutput.sortedBy { -it.score }[0].score
//                                listProb.add(score)

                                storedStr = outputStr
                            }

                    }

                    //////////////////////////////////////////////////////////////////////

                }
                MotionEvent.ACTION_UP -> {
                    //Toast.makeText(applicationContext, "Button Released", Toast.LENGTH_SHORT).show()
                    record.stop()
                    timer.cancel()

                    val maxOccurringGenre = listGenre.groupBy { it }.mapValues { it.value.size }.maxBy { it.value }?.key

                    if (storedStr.isNotEmpty()) {
                        if (maxOccurringGenre.isNullOrEmpty()) {
                            textView.text = "Not Enough Data, Need More"
                            textAnswer.text = ""
                            textAgain.text = "Hold Down Button Again to Restart"
                        } else {
                            textView.text = "This songs genre is"
                            textAnswer.text = maxOccurringGenre
                            textAgain.text = "Hold Down Button Again to Restart"
                        }
                    }
                    else{
                        textView.text = "NO MUSIC DETECTED"
                        textAnswer.text = ""
                        textAgain.text = "Hold Down Button Again to Restart"
                    }

                }
            }
            false
        })

    }



}
