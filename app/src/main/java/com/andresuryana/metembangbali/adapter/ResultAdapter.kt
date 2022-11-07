package com.andresuryana.metembangbali.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.data.model.Tembang
import com.andresuryana.metembangbali.databinding.ItemSongHorizontalSmallBinding
import com.andresuryana.metembangbali.helper.Helpers
import com.bumptech.glide.Glide

class ResultAdapter : RecyclerView.Adapter<ResultAdapter.ViewHolder>() {

    // Define list
    private val list = ArrayList<Tembang>()

    // Item click listener callback
    private lateinit var onItemClickListener: ((tembang: Tembang) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSongHorizontalSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    fun setList(list: ArrayList<Tembang>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun removeList() {
        this.list.clear()
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(onItemClickListener: (tembang: Tembang) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    inner class ViewHolder(private val binding: ItemSongHorizontalSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(tembang: Tembang) {
            binding.tvTitle.text = tembang.title
            binding.tvCategory.text = Helpers.formatCategory(tembang)
            tembang.coverUrl?.let { url ->
                Glide.with(binding.root)
                    .load(url)
                    .placeholder(R.drawable.ic_cover_placeholder)
                    .error(R.drawable.ic_cover_placeholder)
                    .centerCrop()
                    .into(binding.ivCover)
            }
            binding.root.setOnClickListener {
                onItemClickListener.invoke(tembang)
            }
        }
    }
}