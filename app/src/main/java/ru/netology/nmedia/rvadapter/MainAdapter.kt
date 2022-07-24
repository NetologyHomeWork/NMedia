package ru.netology.nmedia.rvadapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostItemBinding
import ru.netology.nmedia.model.Post
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
            cbLike.text = formatCount(post.likesCount)
            buttonShare.text = formatCount(post.shareCount)
            tvViewsCount.text = formatCount(post.viewsCount)
            cbLike.isChecked = post.isLike

            if (post.content.contains("https://youtu.be") or post.content.contains("https://www.youtube.com")) {
                videoView.visibility = View.VISIBLE
            } else {
                videoView.visibility = View.GONE
            }

            cbLike.setOnClickListener {
                listener.onClickLike(post)
            }

            buttonShare.setOnClickListener {
                listener.onClickShare(post)
            }

            ivMore.setOnClickListener {
                showMenu(it, post)
            }

            if (videoView.visibility == View.VISIBLE) {
                videoView.setOnClickListener {
                    listener.onClickUrlVideo(post)
                }
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

