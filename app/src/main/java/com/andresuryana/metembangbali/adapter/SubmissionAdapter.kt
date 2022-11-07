package com.andresuryana.metembangbali.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.andresuryana.metembangbali.R
import com.andresuryana.metembangbali.data.model.Submission
import com.andresuryana.metembangbali.databinding.ItemSongHorizontalSmallBinding
import com.andresuryana.metembangbali.helper.Helpers
import com.andresuryana.metembangbali.utils.SubmissionStatusConstants.STATUS_ACCEPTED
import com.andresuryana.metembangbali.utils.SubmissionStatusConstants.STATUS_PENDING
import com.andresuryana.metembangbali.utils.SubmissionStatusConstants.STATUS_REJECTED
import com.bumptech.glide.Glide

class SubmissionAdapter : RecyclerView.Adapter<SubmissionAdapter.ViewHolder>() {

    // Define list
    private val list = ArrayList<Submission>()

    // Item click listener callback
    private lateinit var onItemClickListener: ((submission: Submission) -> Unit)

    // Item delete listener callback
    private lateinit var onItemDeleteListener: ((submission: Submission) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemSongHorizontalSmallBinding.inflate(
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

    fun setList(list: ArrayList<Submission>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun removeItemAt(position: Int) {
        this.list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItemPosition(submission: Submission): Int = list.indexOf(submission)

    fun setOnItemClickListener(onItemClickListener: (submission: Submission) -> Unit) {
        this.onItemClickListener = onItemClickListener
    }

    fun setOnDeleteClickListener(onItemDeleteListener: (submission: Submission) -> Unit) {
        this.onItemDeleteListener = onItemDeleteListener
    }

    inner class ViewHolder(private val binding: ItemSongHorizontalSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(submission: Submission) {
            // Set title and category
            binding.tvTitle.text = submission.title
            binding.tvCategory.text = Helpers.formatSubmissionCategory(submission)

            // Set cover url
            submission.coverUrl?.let { url ->
                Glide.with(binding.root)
                    .load(url)
                    .placeholder(R.drawable.ic_cover_placeholder)
                    .error(R.drawable.ic_cover_placeholder)
                    .centerCrop()
                    .into(binding.ivCover)
            }

            // Status text
            binding.tvStatus.apply {
                visibility = View.VISIBLE
                text = submission.status?.replaceFirstChar { it.uppercase() }
                setTextColor(
                    ContextCompat.getColor(context,
                        when (submission.status) {
                            STATUS_ACCEPTED -> R.color.color_success
                            STATUS_REJECTED -> R.color.color_danger
                            STATUS_PENDING -> R.color.color_warning
                            else -> R.color.color_text_secondary
                        }
                    )
                )
            }

            // Setup button delete
            binding.btnDelete.visibility = View.VISIBLE
            binding.btnDelete.setOnClickListener {
                onItemDeleteListener.invoke(submission)
            }

            // Setup item clicked
            binding.root.setOnClickListener {
                onItemClickListener.invoke(submission)
            }
        }
    }
}