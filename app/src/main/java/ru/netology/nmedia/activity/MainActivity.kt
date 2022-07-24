package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.rvadapter.AdapterListener
import ru.netology.nmedia.rvadapter.MainAdapter
import ru.netology.nmedia.viewmodel.MainViewModel
import ru.netology.nmedia.viewmodel.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding ?: throw RuntimeException("ActivityMainBinding is null")


    private val factory by lazy { ViewModelFactory(application) }
    private val mainViewModel by viewModels<MainViewModel> { factory }

    private lateinit var postLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
            .also { setContentView(it.root) }
        postLauncher = registerForActivityResult(NewPostActivityContract()) {
            if (it.isNullOrBlank()) {
                mainViewModel.editingClear()
                return@registerForActivityResult
            }
            mainViewModel.editedContent(it)
            mainViewModel.save()
        }
        setupRecyclerView()
        binding.buttonAdd.setOnClickListener {
            postLauncher.launch("")
        }
    }

    private fun setupRecyclerView() {
        val rvPostItem = binding.rvPostList
        val adapter = MainAdapter(
            object : AdapterListener {
                override fun onClickLike(post: Post) {
                    mainViewModel.like(post)
                }

                override fun onClickShare(post: Post) {
                    val intent = mainViewModel.share(post)
                    val shareIntent = Intent.createChooser(intent, getString(R.string.share))
                    startActivity(shareIntent)
                }

                override fun onClickDelete(post: Post) {
                    mainViewModel.deletePost(post.id)
                }

                override fun onClickEdit(post: Post) {
                    editor(post)
                }

                override fun onClickUrlVideo(post: Post) {
                    val intent = mainViewModel.launchYoutubeVideo(post)
                    val shareIntent = Intent.createChooser(intent, getString(R.string.watch))
                    startActivity(shareIntent)
                }

            }
        )
        rvPostItem.adapter = adapter

        mainViewModel.data.observe(this) {
            adapter.submitList(it)
        }
    }

    private fun editor(post: Post) {
        mainViewModel.edit(post)
        postLauncher.launch(post.content)

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}