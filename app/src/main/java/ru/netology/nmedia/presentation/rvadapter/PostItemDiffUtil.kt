package ru.netology.nmedia.presentation.rvadapter

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nmedia.domain.model.Post

class PostItemDiffUtil : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Post, newItem: Post): Any {
        return Unit
    }
}