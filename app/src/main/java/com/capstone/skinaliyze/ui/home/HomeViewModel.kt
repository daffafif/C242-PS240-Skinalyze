package com.capstone.skinaliyze.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.capstone.skinaliyze.R
import com.capstone.skinaliyze.data.remote.response.DataItem
import com.capstone.skinaliyze.data.remote.response.SkincareResponse
import com.capstone.skinaliyze.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _itemRow = MutableLiveData<List<DataItem>>()
    val itemRow: LiveData<List<DataItem>> = _itemRow

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val allProducts = mutableListOf<DataItem>()

    init {
        getEvents()
    }

    private fun getEvents() {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getSkincare()
        client.enqueue(object : Callback<SkincareResponse> {
            override fun onResponse(
                call: Call<SkincareResponse>,
                response: Response<SkincareResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val skinalyzeResponse = response.body()
                    val events = skinalyzeResponse?.data?.map { product ->
                        DataItem(
                            id = product?.id,
                            title = product?.title,
                            description = product?.description,
                            foto = product?.foto,
                            howToUse = product?.howToUse,
                            skinType = product?.skinType
                        )
                    } ?: emptyList()

                    allProducts.clear()
                    allProducts.addAll(events)

                    _itemRow.value = events
                } else {
                    Log.e(TAG, "Response failed: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SkincareResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "API Failed: ${t.message.toString()}")
            }
        })
    }

    fun filterProducts(query: String) {
        if (query.isEmpty()) {
            _itemRow.value = allProducts
            return
        }

        val filteredList = allProducts.filter { product ->
            product.title?.contains(query, ignoreCase = true) == true
        }

        _itemRow.value = filteredList
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}
