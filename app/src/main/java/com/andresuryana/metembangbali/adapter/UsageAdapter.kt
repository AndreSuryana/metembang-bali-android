package com.andresuryana.metembangbali.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.metembangbali.data.model.Usage
import com.andresuryana.metembangbali.databinding.LayoutItemUsageBinding
import com.andresuryana.metembangbali.utils.Ext.spaceCamelCase

class UsageAdapter : RecyclerView.Adapter<UsageAdapter.ViewHolder>() {

    // Define list
    private val list = ArrayList<Usage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutItemUsageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun setList(list: ArrayList<Usage>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun addItem(usage: Usage) {
        val position = list.size /* Index inserted item */
        this.list.add(position, usage)
        notifyItemInserted(position)
    }

    fun removeItemAt(position: Int) {
        this.list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getAllItem(): ArrayList<Usage> = list

    inner class ViewHolder(private val binding: LayoutItemUsageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun onBind(usage: Usage) {
            // Set text
            binding.tvUsage.text = "${usage.activity} - ${usage.typeId?.spaceCamelCase()}"

            // Button delete listener
            binding.btnDelete.setOnClickListener {
                removeItemAt(adapterPosition)
            }
        }
    }
}