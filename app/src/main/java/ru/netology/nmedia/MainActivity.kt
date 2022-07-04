package ru.netology.nmedia

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw RuntimeException("ActivityMainBinding is null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post = Post(
            author = getString(R.string.post_author),
            published = getString(R.string.post_published),
            content = getString(R.string.post_content),
            authorAvatar = R.drawable.ic_netology,
            shareCount = 5,
            likesCount = 10,
            viewsCount = 50
        )

        binding.apply {
            ivIcon.setImageResource(post.authorAvatar)
            tvTitle.text = post.author
            tvDate.text = post.published
            tvPost.text = post.content
            tvLikeCount.text = formatCount(post.likesCount)
            tvShareCount.text = formatCount(post.shareCount)
            tvViewsCount.text = formatCount(post.viewsCount)

            ivLike.setOnClickListener {
                liked(post)
            }

            ivShare.setOnClickListener {
                post.shareCount++
                tvShareCount.text = formatCount(post.shareCount)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun liked(post: Post) {
        if (!post.isLike){
            binding.ivLike.setImageResource(R.drawable.ic_favorite_24)
            post.likesCount++
        }
        else {
            binding.ivLike.setImageResource(R.drawable.ic_favorite_border_24)
            post.likesCount--
        }
        binding.tvLikeCount.text = formatCount(post.likesCount)
        post.isLike = !post.isLike
    }
}