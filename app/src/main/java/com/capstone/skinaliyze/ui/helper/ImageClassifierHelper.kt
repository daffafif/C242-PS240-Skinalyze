package com.capstone.skinaliyze.ui.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import com.capstone.skinaliyze.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

class ImageClassifierHelper(private val context: Context) {

    private var model: Model? = null

    init {
        setupModel(context)
    }

    private fun setupModel(context: Context) {
        model = Model.newInstance(context)
    }

    fun classifyStaticImage(imageUri: Uri, callback: (FloatArray, Float) -> Unit) {
        val bitmap = loadBitmapFromUri(imageUri)
        bitmap?.let {
            try {
                val tensorImage = TensorImage.fromBitmap(it)

                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)

                val resizedBitmap = Bitmap.createScaledBitmap(it, 224, 224, true)

                val byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
                byteBuffer.order(java.nio.ByteOrder.nativeOrder())

                // convert RGB to float32
                for (i in 0 until 224) {
                    for (j in 0 until 224) {
                        val pixel = resizedBitmap.getPixel(j, i)
                        byteBuffer.putFloat(((pixel shr 16) and 0xFF) / 255.0f) // Red
                        byteBuffer.putFloat(((pixel shr 8) and 0xFF) / 255.0f)  // Green
                        byteBuffer.putFloat((pixel and 0xFF) / 255.0f)          // Blue
                    }
                }

                inputFeature0.loadBuffer(byteBuffer)

                val outputs = model?.process(inputFeature0)
                val outputFeature0 = outputs?.outputFeature0AsTensorBuffer

                val result = outputFeature0?.floatArray ?: FloatArray(5) { 0f }

                val confidence = result.maxOrNull() ?: 0f

                callback(result, confidence)

            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(context, "Gagal memuat gambar", Toast.LENGTH_SHORT).show()
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val source = ImageDecoder.createSource(context.contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        } else {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        }

        return bitmap.copy(Bitmap.Config.ARGB_8888, true)
    }
}