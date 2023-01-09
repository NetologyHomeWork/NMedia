package ru.netology.nmedia.data.utils

import android.view.View
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.bumptech.glide.Glide
import ru.netology.nmedia.BuildConfig
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.PostItemBinding
import ru.netology.nmedia.domain.model.AttachmentType
import ru.netology.nmedia.domain.model.Post
import ru.netology.nmedia.domain.model.PostUIModel
import ru.netology.nmedia.presentation.fragments.PostDetailFragmentDirections
import ru.netology.nmedia.presentation.rvadapter.AdapterListener
import ru.netology.nmedia.presentation.viewmodel.PostDetailViewModel

fun bindPostItemLayout(
    binding: PostItemBinding,
    post: PostUIModel,
    listener: AdapterListener
) {
    bindingItem(binding, post)
    adapterListenerAction(binding, post, listener)
}

fun bindPostItemLayout(
    binding: PostItemBinding,
    post: PostUIModel,
    viewModel: PostDetailViewModel,
    navController: NavController
) {
    bindingItem(binding, post)
    viewModelListenerAction(binding, post, viewModel, navController)
}

private fun showMenuInListItem(view: View, post: PostUIModel, listener: AdapterListener) {
    PopupMenu(view.context, view).apply {
        inflate(R.menu.post_options)
        if (post.isError) {
            this.menu.findItem(R.id.retry).isVisible = true
        }
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
                R.id.retry -> {
                    listener.onClickRetry(post)
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
    viewModel: PostDetailViewModel,
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
                        PostDetailFragmentDirections.actionPostDetailFragmentToPostEditFragment(post)
                    )
                    true
                }
                else -> false
            }
        }
    }.show()
}

private fun bindingItem(binding: PostItemBinding, post: PostUIModel) {
    binding.apply {
        tvTitle.text = post.post.author
        tvDate.text = post.post.published.formatDate()
        tvPost.text = post.post.content
        cbLike.text = formatCount(post.post.likesCount)
        cbLike.isChecked = post.post.isLike
        ivError.isVisible = post.isError
        cbLike.isEnabled = post.isError.not()
        buttonShare.isEnabled = post.isError.not()
        binding.root.isEnabled = post.isError.not()

        Glide.with(ivIcon)
            .load("${BuildConfig.BASE_URL}avatars/${post.post.authorAvatar}")
            .timeout(10_000)
            .placeholder(R.drawable.ic_loading)
            .error(R.drawable.ic_error)
            .circleCrop()
            .into(ivIcon)

        if (post.post.attachment != null && post.post.attachment.type == AttachmentType.IMAGE) {
            ivAttachmentPicture.isVisible = true

            Glide.with(ivAttachmentPicture)
                .load("${BuildConfig.BASE_URL}media/${post.post.attachment.url}")
                .timeout(10_000)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_error)
                .into(ivAttachmentPicture)
        } else ivAttachmentPicture.isVisible = false
    }
}

private fun adapterListenerAction(binding: PostItemBinding, post: PostUIModel, listener: AdapterListener) {
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

        binding.ivAttachmentPicture.setOnClickListener {
            listener.onPhotoClick(post)
        }
    }
}

private fun viewModelListenerAction(
    binding: PostItemBinding,
    post: PostUIModel,
    viewModel: PostDetailViewModel,
    navController: NavController
) {
    with(binding) {
        cbLike.setOnClickListener {
            viewModel.like(post.post)
        }

        ivMore.setOnClickListener {
            showMenuInDetailItem(it, post.post, viewModel, navController)
        }
    }
}