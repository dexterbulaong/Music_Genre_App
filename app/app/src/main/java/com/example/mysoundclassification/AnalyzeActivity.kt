package com.example.mysoundclassification

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import android.os.Handler
import java.io.IOException


class AnalyzeActivity : AppCompatActivity(){

    var TAG = "AnalyzeActivity"

    // path for our model
    var modelPath = "genre_classifier_model.tflite"

    var probabilityThreshold: Float = 0.3f

    lateinit var textView: TextView

    var timer = Timer()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze)

        timer = Timer()

        textView = findViewById<TextView>(R.id.output)

        val classifier = AudioClassifier.createFromFile(this, modelPath)

        val tensor = classifier.createInputTensorAudio()

        val record = classifier.createAudioRecord()

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

            val outputStr =
                filteredModelOutput.sortedBy { -it.score }
                    .joinToString(separator = "\n") { "${it.label} -> ${it.score} " }


            if (outputStr.isNotEmpty())
                runOnUiThread {
                    textView.text = outputStr
                }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        timer.cancel()
        //record.stop()
        super.finish()
    }


}