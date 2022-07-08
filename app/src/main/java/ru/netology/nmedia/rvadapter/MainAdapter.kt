package ru.netology.nmedia.rvadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostItemBinding
import ru.netology.nmedia.utils.formatCount

class MainAdapter(
    private val listener: AdapterListener
) : ListAdapter<Post, MainAdapter.MainViewHolder>(PostItemDiffUtil()) {

    inner class MainViewHolder(
        private val binding: PostItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            bindLayout(binding, post)
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

    private fun bindLayout(binding: PostItemBinding, post: Post) {
        binding.apply {
            ivIcon.setImageResource(post.authorAvatar)
            tvTitle.text = post.author
            tvDate.text = post.published
            tvPost.text = post.content
            tvLikeCount.text = formatCount(post.likesCount)
            tvShareCount.text = formatCount(post.shareCount)
            tvViewsCount.text = formatCount(post.viewsCount)
            ivLike.setImageResource(
                if (post.isLike) R.drawable.ic_favorite_24 else R.drawable.ic_favorite_border_24
            )

            ivLike.setOnClickListener {
                listener.onClickLike(post)
            }

            ivShare.setOnClickListener {
                listener.onClickShare(post)
            }

            ivMore.setOnClickListener {
                showMenu(it, post)
            }
        }
    }

    private fun showMenu(view: View, post: Post) {
        PopupMenu(view.context, view).apply {
            inflate(R.menu.post_options)
            setOnMenuItemClickListener { item ->
                when(item.itemId) {
                    R.id.remove -> {
                        listener.onClickDelete(post)
                        true
                    }
                    R.id.item_edit -> {
                        listener.onClickEdit(post)
                        true
                    }
                    else -> false
                }
            }
        }.show()
    }
}

