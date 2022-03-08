package com.example.mysoundclassification

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class AnalyzeActivity : AppCompatActivity(){

    var TAG = "AnalyzeActivity"

    // path for our model
    var modelPath = "genre_classifier_model.tflite"

    var probabilityThreshold: Float = 0.3f

    lateinit var textView: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analyze)

        val REQUEST_RECORD_AUDIO = 1337
        requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO)

        textView = findViewById<TextView>(R.id.output)
//        val recorderSpecsTextView = findViewById<TextView>(R.id.textViewAudioRecorderSpecs)

        val classifier = AudioClassifier.createFromFile(this, modelPath)

        val tensor = classifier.createInputTensorAudio()

        val format = classifier.requiredTensorAudioFormat
//        val recorderSpecs = "Number Of Channels: ${format.channels}\n" +
//                "Sample Rate: ${format.sampleRate}"
//        recorderSpecsTextView.text = recorderSpecs

        val record = classifier.createAudioRecord()
        record.startRecording()

        Timer().scheduleAtFixedRate(1, 1000) {

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


}