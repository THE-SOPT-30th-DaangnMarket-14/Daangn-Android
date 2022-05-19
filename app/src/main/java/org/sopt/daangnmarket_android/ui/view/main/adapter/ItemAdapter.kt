package org.sopt.daangnmarket_android.ui.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.sopt.daangnmarket_android.databinding.ItemListBinding
import org.sopt.daangnmarket_android.ui.view.data.ItemData

class ItemAdapter :  RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    val itemList = mutableListOf<ItemData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.onBind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    class ItemViewHolder(
        private val binding: ItemListBinding
    ) : RecyclerView.ViewHolder(binding.root){
        fun onBind(data: ItemData){
            binding.tvListTitle.text = data.title
            binding.tvListContent.text = data.content
            binding.tvListPrice.text = data.price
            binding.tvHeartCount.text = data.heart
        }
    }
}