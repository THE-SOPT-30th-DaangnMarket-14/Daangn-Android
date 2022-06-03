package org.sopt.daangnmarket_android.ui.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.sopt.daangnmarket_android.databinding.ItemListBinding
import org.sopt.daangnmarket_android.ui.view.data.ItemData
import java.text.DecimalFormat

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
            //천 단위 콤마 추가
            val decimal = DecimalFormat("#,###")
            with(binding){
                tvListTitle.text = data.title
                tvListLocation.text = data.location
                tvListPrice.text = decimal.format(data.price).toString() + "원"
                Glide.with(binding.root)
                    .load(data.image)
                    .into(binding.ivHomeList)
                ivHomeList.clipToOutline = true
                tvLikeCount.text = decimal.format(data.likeCount).toString()
                tvListTimeBefore.text = data.timeBefore
            }
        }
    }
}