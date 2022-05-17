package org.sopt.daangnmarket_android.ui.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.sopt.daangnmarket_android.R
import org.sopt.daangnmarket_android.databinding.ItemGalleryBinding

class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder>() {
    private val asyncDiffer = AsyncListDiffer(this, diffCallback)

    class GalleryViewHolder(private val binding: ItemGalleryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Bitmap) {
            binding.ivGallery.setImageBitmap(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = DataBindingUtil.inflate<ItemGalleryBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_gallery,
            parent,
            false
        )
        return GalleryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(asyncDiffer.currentList[position])
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size

    fun replaceItem(itemList: List<Bitmap>) {
        asyncDiffer.submitList(itemList)
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Bitmap>() {
            override fun areItemsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Bitmap, newItem: Bitmap): Boolean {
                return oldItem.sameAs(newItem)
            }
        }
    }
}