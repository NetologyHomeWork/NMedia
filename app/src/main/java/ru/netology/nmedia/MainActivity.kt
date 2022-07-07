package ru.netology.nmedia

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw RuntimeException("ActivityMainBinding is null")

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val rvPostItem = binding.rvPostList
        val adapter = MainAdapter()
        rvPostItem.adapter = adapter
        adapter.onClickLike = {
            mainViewModel.like(it)
        }
        adapter.onClickShare = {
            mainViewModel.share(it)
        }
        mainViewModel.data.observe(this) {
            adapter.submitList(it)
            binding.apply {
                ivIcon.setImageResource(it.authorAvatar)
                tvTitle.text = it.author
                tvDate.text = it.published
                tvPost.text = it.content
                tvLikeCount.text = formatCount(it.likesCount)
                tvShareCount.text = formatCount(it.shareCount)
                tvViewsCount.text = formatCount(it.viewsCount)
                ivLike.setImageResource(
                    if (it.isLike) R.drawable.ic_favorite_24 else R.drawable.ic_favorite_border_24
                )

                ivLike.setOnClickListener {
                    mainViewModel.like()
                }

                ivShare.setOnClickListener {
                    mainViewModel.share()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}