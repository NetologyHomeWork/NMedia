package ru.netology.nmedia.presentation.rvadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.data.utils.bindPostItemLayout
import ru.netology.nmedia.databinding.PostItemBinding
import ru.netology.nmedia.domain.model.PostUIModel

class MainAdapter(
    private val isAuth: Boolean,
    private val listener: AdapterListener
) : ListAdapter<PostUIModel, MainAdapter.MainViewHolder>(PostItemDiffUtil()) {

    inner class MainViewHolder(
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostUIModel) {
            bindPostItemLayout(binding, post, listener, isAuth)
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val binding = PostItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MainViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

