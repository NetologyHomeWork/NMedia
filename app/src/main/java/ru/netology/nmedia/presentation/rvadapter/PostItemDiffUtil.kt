package ru.netology.nmedia.presentation.rvadapter

import androidx.recyclerview.widget.DiffUtil
import ru.netology.nmedia.domain.model.PostUIModel

class PostItemDiffUtil : DiffUtil.ItemCallback<PostUIModel>() {

    override fun areItemsTheSame(oldItem: PostUIModel, newItem: PostUIModel): Boolean {
        return oldItem.post.id == newItem.post.id
    }

    override fun areContentsTheSame(oldItem: PostUIModel, newItem: PostUIModel): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: PostUIModel, newItem: PostUIModel): Any {
        return Unit
    }
}