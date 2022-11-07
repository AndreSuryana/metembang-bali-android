package com.andresuryana.metembangbali.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.data.model.Tembang
import com.andresuryana.metembangbali.databinding.ItemSongHorizontalMediumBinding
import com.andresuryana.metembangbali.helper.Helpers.formatCategory
import com.bumptech.glide.Glide

class MostViewAdapter : RecyclerView.Adapter<MostViewAdapter.ViewHolder>() {

    // Define list
    private val list = ArrayList<Tembang>()

    // Item click listener callback
    private lateinit var onItemClickListener: ((tembang: Tembang) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSongHorizontalMediumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setList(list: ArrayList<Tembang>) {
        this.list.clear()
        this.list.addAll(list)
    }

    fun setOnItemClickListener(onItemClickListener: (tembang: Tembang) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    inner class ViewHolder(private val binding: ItemSongHorizontalMediumBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(tembang: Tembang) {
            binding.tvRank.text = "#${adapterPosition + 1}"
            binding.tvTitle.text = tembang.title
            binding.tvCategory.text = formatCategory(tembang)
            binding.tvViewCount.text = tembang.viewCount.toString()
            Glide.with(binding.root)
                .load(tembang.coverUrl)
                .placeholder(R.drawable.ic_cover_placeholder)
                .error(R.drawable.ic_cover_placeholder)
                .centerCrop()
                .into(binding.ivCover)
            binding.root.setOnClickListener {
                onItemClickListener.invoke(tembang)
            }
        }
    }
}