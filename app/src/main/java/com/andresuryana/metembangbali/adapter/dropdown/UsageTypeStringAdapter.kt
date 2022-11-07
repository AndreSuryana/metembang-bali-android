package com.andresuryana.metembangbali.adapter.dropdown

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.data.model.UsageType
import com.andresuryana.metembangbali.databinding.ItemDropdownBinding

class UsageTypeStringAdapter(
    ctx: Context,
    resId: Int = R.layout.item_dropdown,
    private val list: ArrayList<UsageType>
) : ArrayAdapter<UsageType>(ctx, resId, list) {

    override fun getCount(): Int = list.size

    override fun getItem(position: Int): UsageType = list[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val binding: ItemDropdownBinding
        var view = convertView

        // Inflate layout
        if (view == null) {
            binding =
                ItemDropdownBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            view = binding.root
        } else {
            binding = ItemDropdownBinding.bind(view)
        }

        // Set text value
        binding.root.text = list[position].name

        return view
    }
}