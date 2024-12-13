package com.capstone.skinaliyze.ui.camera

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.capstone.skinaliyze.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val output = intent.getFloatArrayExtra("RESULT_ANALYSIS")
        val classNames = arrayOf("Acne", "Actinic Keratosis", "Basal Cell Carcinoma", "Eczema", "Normal")

        if (output != null) {
            val maxIndex = output.indices.maxByOrNull { output[it] } ?: -1
            val resultClass = if (maxIndex != -1) classNames[maxIndex] else "Unknown"
            val maxConfidence = output[maxIndex] * 100

            binding.resultText.text = "Analysis Result: $resultClass\nConfidence Score: ${String.format("%.2f", maxConfidence)}%"

            val imageUri = intent.getStringExtra("IMAGE_URI")?.let { Uri.parse(it) }
            imageUri?.let {
                binding.resultImage.setImageURI(it)
            }
        } else {
            binding.resultText.text = "Hasil analisis tidak ditemukan"
        }

        // Menampilkan hasil debug ke log
        Log.d("ResultActivity", "Hasil Analisis: ${binding.resultText.text}")
    }
}