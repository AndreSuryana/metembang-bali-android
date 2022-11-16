package com.andresuryana.metembangbali.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.metembangbali.databinding.ItemLyricsBinding
import com.andresuryana.metembangbali.utils.Constants.APP_FONT
import com.andresuryana.metembangbali.utils.Constants.SHARED_PREFS_KEY

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
            binding.tvLyrics.apply {
                text = lyrics
                textSize = getTextSize(context)
            }
            binding.tvLyricsIdn.visibility = View.INVISIBLE
            if (!lyricsIDN.isNullOrBlank()) {
                binding.tvLyricsIdn.text = lyricsIDN
            }
        }
    }

    private fun getTextSize(context: Context): Float {
        // Shared prefs
        val prefs = context.getSharedPreferences(SHARED_PREFS_KEY, Context.MODE_PRIVATE)

        return when (prefs.getInt(APP_FONT, 1 /* Normal */)) {
            0 -> 14f /* Small */
            1 -> 18f /* Normal */
            2 -> 24f /* Large */
            else -> 18f /* Normal */
        }
    }
}