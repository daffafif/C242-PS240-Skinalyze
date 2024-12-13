package com.capstone.skinaliyze.ui.camera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.capstone.skinaliyze.R
import com.capstone.skinaliyze.databinding.FragmentCameraBinding
import com.capstone.skinaliyze.ui.helper.ImageClassifierHelper
import com.capstone.skinaliyze.ui.helper.getImageUri

class CameraFragment : Fragment(R.layout.fragment_camera) {
    private lateinit var binding: FragmentCameraBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private val imageViewModel: CameraViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCameraBinding.bind(view)

        imageClassifierHelper = ImageClassifierHelper(requireContext())

        binding.cameraButton.setOnClickListener {
            startCamera()
        }

        binding.galleryButton.setOnClickListener {
            startGallery()
        }

        binding.analyzeButton.setOnClickListener {
            analyzeImage()
        }

        imageViewModel.imageUri?.let { uri ->
            binding.previewImageView.setImageURI(uri)
        }
    }

    private fun startCamera() {
        imageViewModel.imageUri = getImageUri(requireContext())
        launcherIntentCamera.launch(imageViewModel.imageUri!!)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        } else {
            imageViewModel.imageUri = null
        }
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    imageViewModel.imageUri = uri
                    showImage()
                }
            } else {
                showToast("Failed to pick image.")
            }
        }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private fun showImage() {
        imageViewModel.imageUri?.let { uri ->
            binding.previewImageView.setImageURI(uri)
        } ?: showToast("Image not found.")
    }

    private fun analyzeImage() {
        imageViewModel.imageUri?.let { uri ->
            imageClassifierHelper.classifyStaticImage(uri) { result, confidence ->
                moveToResult(result, confidence)
            }
        } ?: showToast("Please select an image first.")
    }

    private fun moveToResult(result: FloatArray, confidence: Float) {
        val classNames = arrayOf("Acne", "Actinic Keratosis", "Basal Cell Carcinoma", "Eczema", "Normal")
        val maxIndex = result.indices.maxByOrNull { result[it] } ?: -1
        val resultClass = if (maxIndex != -1) classNames[maxIndex] else "Unknown"

        val intent = Intent(requireContext(), ResultActivity::class.java).apply {
            putExtra("RESULT_TEXT", resultClass)
            putExtra("CONFIDENCE_SCORE", confidence)
            putExtra("IMAGE_URI", imageViewModel.imageUri.toString())
            putExtra("RESULT_ANALYSIS", result)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}