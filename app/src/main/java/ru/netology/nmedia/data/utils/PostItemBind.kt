package ru.netology.nmedia.data.utils

import android.view.View
import android.widget.PopupMenu
import androidx.navigation.NavController
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostItemBinding
import ru.netology.nmedia.presentation.fragments.PostDetailFragmentDirections
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.presentation.rvadapter.AdapterListener
import ru.netology.nmedia.presentation.viewmodel.MainViewModel

fun bindPostItemLayout(
    binding: PostItemBinding,
    post: Post,
    listener: AdapterListener
) {
    bindingItem(binding, post)
    adapterListenerAction(binding, post, listener)
}

fun bindPostItemLayout(
    binding: PostItemBinding,
    post: Post,
    viewModel: MainViewModel,
    navController: NavController
) {
    bindingItem(binding, post)
    viewModelListenerAction(binding, post, viewModel, navController)
}

private fun showMenuInListItem(view: View, post: Post, listener: AdapterListener) {
    PopupMenu(view.context, view).apply {
        inflate(R.menu.post_options)
        setOnMenuItemClickListener { item ->
            when (item.itemId) {
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

private fun showMenuInDetailItem(
    view: View,
    post: Post,
    viewModel: MainViewModel,
    navController: NavController
) {
    PopupMenu(view.context, view).apply {
        inflate(R.menu.post_options)
        setOnMenuItemClickListener { item ->
            when(item.itemId) {
                R.id.remove -> {
                    viewModel.deletePost(post.id)
                    navController.popBackStack()
                    true
                }
                R.id.item_edit -> {
                    viewModel.edit(post)
                    navController.navigate(
                        PostDetailFragmentDirections.actionPostDetailFragmentToPostEditFragment(post.content)
                    )
                    true
                }
                else -> false
            }
        }
    }.show()
}

private fun bindingItem(binding: PostItemBinding, post: Post) {
    binding.apply {
        tvTitle.text = post.author
        tvDate.text = post.published.formatDate()
        tvPost.text = post.content
        cbLike.text = formatCount(post.likesCount)
        cbLike.isChecked = post.isLike

        if (post.content.contains("https://youtu.be") or post.content.contains("https://www.youtube.com")) {
            videoView.visibility = View.VISIBLE
        } else {
            videoView.visibility = View.GONE
        }
    }
}

private fun adapterListenerAction(binding: PostItemBinding, post: Post, listener: AdapterListener) {
    with(binding) {
        cbLike.setOnClickListener {
            listener.onClickLike(post)
        }

        buttonShare.setOnClickListener {
            listener.onClickShare(post)
        }

        ivMore.setOnClickListener {
            showMenuInListItem(it, post, listener)
        }

        binding.root.setOnClickListener {
            listener.onClickPost(post)
        }

        if (videoView.visibility == View.VISIBLE) {
            videoView.setOnClickListener {
                listener.onClickUrlVideo(post)
            }
        }
    }
}

private fun viewModelListenerAction(
    binding: PostItemBinding,
    post: Post,
    viewModel: MainViewModel,
    navController: NavController
) {
    with(binding) {
        cbLike.setOnClickListener {
            viewModel.like(post)
        }

        ivMore.setOnClickListener {
            showMenuInDetailItem(it, post, viewModel, navController)
        }
    }
}