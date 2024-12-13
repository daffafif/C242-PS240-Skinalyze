package com.capstone.skinaliyze.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.skinaliyze.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val productTitle = intent.getStringExtra("PRODUCT_TITLE")
        val productDescriptionText = intent.getStringExtra("PRODUCT_DESCRIPTION")
        val productImageUrl = intent.getStringExtra("PRODUCT_FOTO")
        val productHowToUse = intent.getStringArrayListExtra("PRODUCT_HOW_TO_USE")
        val productSkinTypeList = intent.getStringArrayListExtra("PRODUCT_SKIN_TYPE")

        binding.productName.text = productTitle ?: "No Title"
        binding.productDescription.text = productDescriptionText ?: "No Description"
        Glide.with(this).load(productImageUrl).into(binding.productImage)

        if (productHowToUse != null && productHowToUse.isNotEmpty()) {
            binding.howToUse.text = productHowToUse.joinToString("\n")
        } else {
            binding.howToUse.text = "No instructions available"
        }

        if (productSkinTypeList != null && productSkinTypeList.isNotEmpty()) {
            binding.productSkinType.text = productSkinTypeList.joinToString("\n")
        } else {
            binding.productSkinType.text = "No skin types available"
        }
    }
}
