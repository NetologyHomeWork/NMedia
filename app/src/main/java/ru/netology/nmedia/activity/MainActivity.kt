package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.*
import ru.netology.nmedia.databinding.ActivityMainBinding
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.rvadapter.AdapterListener
import ru.netology.nmedia.rvadapter.MainAdapter
import ru.netology.nmedia.viewmodel.MainViewModel

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
        binding.icSend.setOnClickListener {
            binding.etEditPost.apply {
                if (text.isNullOrBlank()) {
                    Toast.makeText(
                        this@MainActivity,
                        R.string.empty_content,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }
                mainViewModel.editedContent(text.toString())
                mainViewModel.save()
                setText("")
                clearFocus()
                binding.editLayout.visibility = View.GONE
                hideKeyboard(it)
            }
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
                    mainViewModel.share(post)
                }

                override fun onClickDelete(post: Post) {
                    mainViewModel.deletePost(post.id)
                }

                override fun onClickEdit(post: Post) {
                    editor(post)
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
        mainViewModel.edited.observe(this) { postObserve ->
            if (postObserve.id == 0L) {
                return@observe
            }
            binding.etEditPost.apply {
                requestFocus()
                setText(postObserve.content.trim())
                text?.length?.let { setSelection(it) }
                showKeyboard(this)
            }
            binding.apply {
                editLayout.visibility = View.VISIBLE
                editTitle.text = postObserve.content
                editClose.setOnClickListener {
                    editLayout.visibility = View.GONE
                    etEditPost.setText("")
                    hideKeyboard(it)
                    mainViewModel.editingClear()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}