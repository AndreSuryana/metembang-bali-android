package com.andresuryana.metembangbali.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.metembangbali.databinding.ItemLyricsBinding

class LyricsAdapter : RecyclerView.Adapter<LyricsAdapter.ViewHolder>() {

    // Layout binding
    private lateinit var binding: ItemLyricsBinding

    // Define list
    private val lyrics = ArrayList<String>()
    private val lyricsIDN = ArrayList<String>()

    fun setList(lyrics: ArrayList<String>, lyricsIDN: ArrayList<String>? = null) {
        this.lyrics.clear()
        this.lyrics.addAll(lyrics)
        if (lyricsIDN != null) {
            this.lyricsIDN.clear()
            this.lyricsIDN.addAll(lyricsIDN)
        } else {
            this.lyricsIDN.clear()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemLyricsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(
            lyrics[position],
            if (lyricsIDN.isNotEmpty()) lyricsIDN[position] else null
        )

    }

    override fun getItemCount(): Int = lyrics.size

    inner class ViewHolder(private val binding: ItemLyricsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(lyrics: String, lyricsIDN: String? = null) {
            binding.tvLyrics.text = lyrics
            binding.tvLyricsIdn.visibility = View.INVISIBLE
            if (!lyricsIDN.isNullOrBlank()) {
                binding.tvLyricsIdn.text = lyricsIDN
            }
        }
    }
}