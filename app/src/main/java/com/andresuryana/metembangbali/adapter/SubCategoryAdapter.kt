package com.andresuryana.metembangbali.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.data.model.SubCategory
import com.andresuryana.metembangbali.databinding.ItemCategoryBinding
import com.andresuryana.metembangbali.utils.CategoryConstants.IMAGES
import com.bumptech.glide.Glide

class SubCategoryAdapter : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

    // Define list
    private val list = ArrayList<SubCategory>()

    // Item click listener callback
    private lateinit var onItemClickListener: ((subCategory: SubCategory) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setList(list: ArrayList<SubCategory>) {
        this.list.clear()
        this.list.addAll(list)
    }

    fun setOnItemClickListener(onItemClickListener: (subCategory: SubCategory) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(subCategory: SubCategory) {
            binding.tvCategoryName.text = subCategory.name
            Glide.with(binding.root)
                .load(IMAGES.getOrDefault(subCategory.id, R.drawable.ic_category_default))
                .placeholder(R.drawable.ic_category_default)
                .error(R.drawable.ic_category_default)
                .centerCrop()
                .into(binding.ivCategoryImage)
            binding.root.setOnClickListener {
                onItemClickListener.invoke(subCategory)
            }
        }
    }
}