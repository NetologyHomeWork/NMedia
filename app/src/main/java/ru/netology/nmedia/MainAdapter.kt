package ru.netology.nmedia

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.PostItemBinding

class MainAdapter(
    private val context: Context
) : ListAdapter<Post, MainAdapter.MainViewHolder>(PostItemDiffUtil()) {

    inner class MainViewHolder(
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            bindLayout(binding, post)
        }
    }

    var onClickLike: ((Post) -> Unit)? = null
    var onClickShare: ((Post) -> Unit)? = null

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

    private fun bindLayout(binding: PostItemBinding, post: Post) {
        binding.apply {
            ivIcon.setImageResource(post.authorAvatar)
            tvTitle.text = context.getString(post.author)
            tvDate.text = context.getString(post.published)
            tvPost.text = context.getString(post.content)
            tvLikeCount.text = formatCount(post.likesCount)
            tvShareCount.text = formatCount(post.shareCount)
            tvViewsCount.text = formatCount(post.viewsCount)
            ivLike.setImageResource(
                if (post.isLike) R.drawable.ic_favorite_24 else R.drawable.ic_favorite_border_24
            )

            ivLike.setOnClickListener {
                onClickLike?.invoke(post)
            }

            ivShare.setOnClickListener {
                onClickShare?.invoke(post)
            }
        }
    }
}

