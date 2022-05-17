package org.sopt.daangnmarket_android.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.sopt.daangnmarket_android.R
import org.sopt.daangnmarket_android.domain.model.GalleryImage
import org.sopt.daangnmarket_android.databinding.ItemCameraBinding
import org.sopt.daangnmarket_android.databinding.ItemGalleryBinding

class GalleryAdapter(private val imageClick: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val asyncDiffer = AsyncListDiffer(this, diffCallback)

    class CameraViewHolder(private val binding: ItemCameraBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {

        }
    }

    class GalleryViewHolder(private val binding: ItemGalleryBinding, private val itemClick: (Int) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GalleryImage) {
            binding.ivGallery.setImageBitmap(item.image)
            binding.image = item

            binding.root.setOnClickListener {
                itemClick(this.layoutPosition)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> CAMERA_VIEW_HOLDER
            else -> GALLERY_VIEW_HOLDER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CAMERA_VIEW_HOLDER -> {
                val binding = ItemCameraBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                CameraViewHolder(binding)
            }
            GALLERY_VIEW_HOLDER -> {
                val binding = DataBindingUtil.inflate<ItemGalleryBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_gallery,
                    parent,
                    false
                )
                GalleryViewHolder(binding, imageClick)
            }
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (position) {
            CAMERA_VIEW_HOLDER -> {
                (holder as CameraViewHolder).bind()
            }
            else -> {
                (holder as GalleryViewHolder).bind(asyncDiffer.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size

    fun replaceItem(itemList: List<GalleryImage?>) {
        asyncDiffer.submitList(itemList.map {
            it?.copy()
        })
    }

    companion object {
        const val CAMERA_VIEW_HOLDER = 0
        const val GALLERY_VIEW_HOLDER = 1

        private val diffCallback = object : DiffUtil.ItemCallback<GalleryImage>() {
            override fun areItemsTheSame(oldItem: GalleryImage, newItem: GalleryImage): Boolean {
                return oldItem.image == newItem.image
            }

            override fun areContentsTheSame(oldItem: GalleryImage, newItem: GalleryImage): Boolean {
                return oldItem == newItem
            }
        }
    }
}