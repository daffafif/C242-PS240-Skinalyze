package com.capstone.skinaliyze.ui.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.skinaliyze.R
import com.capstone.skinaliyze.data.remote.response.DataItem
import com.capstone.skinaliyze.ui.detail.DetailActivity

class ProductAdapter(
    private var listProduct: List<DataItem>,
    private val onProductClick: (DataItem) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = listProduct[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onProductClick(product)
        }
    }

    override fun getItemCount(): Int = listProduct.size

    fun updateList(newList: List<DataItem>) {
        listProduct = newList
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val title: TextView = itemView.findViewById(R.id.title)

        fun bind(product: DataItem) {
            title.text = product.title
            Glide.with(itemView.context)
                .load(product.foto)
                .into(imageView)
        }
    }
}
