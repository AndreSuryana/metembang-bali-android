package com.andresuryana.metembangbali.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.data.model.Category
import com.andresuryana.metembangbali.databinding.ItemCategoryBinding
import com.bumptech.glide.Glide

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    // Define list
    private val list = ArrayList<Category>()

    // Item click listener callback
    private lateinit var onItemClickListener: ((category: Category) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setList(list: ArrayList<Category>) {
        this.list.clear()
        this.list.addAll(list)
    }

    fun setOnItemClickListener(onItemClickListener: (category: Category) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    inner class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(category: Category) {
            binding.tvCategoryName.text = category.name
            Glide.with(binding.root)
                .load(R.drawable.screen_1)
                .placeholder(R.drawable.screen_1)
                .error(R.drawable.screen_1)
                .centerCrop()
                .into(binding.ivCategoryImage)
            binding.root.setOnClickListener {
                onItemClickListener.invoke(category)
            }
        }
    }
}