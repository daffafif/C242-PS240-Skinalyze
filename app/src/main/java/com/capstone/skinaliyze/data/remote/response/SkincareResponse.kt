package com.capstone.skinaliyze.data.remote.response

import com.google.gson.annotations.SerializedName

data class SkincareResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)

data class DataItem(

	@field:SerializedName("foto")
	val foto: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("how_to_use")
	val howToUse: List<String?>? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("skin_type")
	val skinType: List<String?>? = null
)
