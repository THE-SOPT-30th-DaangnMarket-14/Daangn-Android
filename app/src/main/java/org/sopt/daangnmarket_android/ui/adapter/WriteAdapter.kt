package org.sopt.daangnmarket_android.ui.adapter

import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import org.sopt.daangnmarket_android.R
import org.sopt.daangnmarket_android.databinding.ItemWriteCameraBinding
import org.sopt.daangnmarket_android.databinding.ItemWriteImageBinding
import org.sopt.daangnmarket_android.domain.model.GalleryImage

class WriteAdapter(
    private val cameraClick: (Unit) -> Unit,
    private val removeImage: (GalleryImage) -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val asyncDiffer = AsyncListDiffer(this, diffCallback)

    class WriteCameraViewHolder(
        private val binding: ItemWriteCameraBinding,
        private val cameraClick: (Unit) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(itemCount: Int) {
            binding.count = itemCount
        }
    }

    class WriteImageViewHolder(
        private val binding: ItemWriteImageBinding,
        private val removeImage: (GalleryImage) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(galleryImage: GalleryImage) {
            binding.ivWriteImage.setImageBitmap(galleryImage.image)

            binding.ivImageDelete.setOnClickListener {
                removeImage(galleryImage)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> CAMERA_VIEW_HOLDER
            else -> IMAGE_VIEW_HOLDER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            CAMERA_VIEW_HOLDER -> {
                val binding = DataBindingUtil.inflate<ItemWriteCameraBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_write_camera,
                    parent,
                    false
                )
                binding.root.setOnClickListener {
                    cameraClick(Unit)
                }
                return WriteCameraViewHolder(binding, cameraClick)
            }
            IMAGE_VIEW_HOLDER -> {
                val binding = DataBindingUtil.inflate<ItemWriteImageBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.item_write_image,
                    parent,
                    false
                )
                return WriteImageViewHolder(binding, removeImage)
            }
            else -> throw IndexOutOfBoundsException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is WriteCameraViewHolder -> {
                holder.bind(asyncDiffer.currentList.size - 1)
            }
            is WriteImageViewHolder -> {
                holder.bind(asyncDiffer.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size

    fun replaceItem(itemList: List<GalleryImage?>) {
        asyncDiffer.submitList(itemList)
    }

    companion object {
        const val CAMERA_VIEW_HOLDER = 0
        const val IMAGE_VIEW_HOLDER = 1

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