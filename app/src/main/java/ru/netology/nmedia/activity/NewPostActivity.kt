package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityNewPostBinding
import ru.netology.nmedia.utils.Constants

class NewPostActivity : AppCompatActivity() {

    private var _binding: ActivityNewPostBinding? = null
    private val binding: ActivityNewPostBinding
        get() = _binding ?: throw NullPointerException("ActivityNewPostBinding is null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNewPostBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.buttonSave.setOnClickListener { save() }
    }

    private fun save() {
            if (binding.etEditPost.text.isNullOrBlank()) {
                Toast.makeText(
                    this@NewPostActivity,
                    R.string.empty_content,
                    Toast.LENGTH_SHORT
                ).show()
                setResult(RESULT_CANCELED)
            } else {
                val result = Intent().putExtra(
                    Constants.EXTRA_POST_INTENT,
                    binding.etEditPost.text.toString()
                )
                setResult(RESULT_OK, result)
            }
            finish()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}