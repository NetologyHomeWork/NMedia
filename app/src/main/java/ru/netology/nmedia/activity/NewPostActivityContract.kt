package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import ru.netology.nmedia.utils.Constants
import ru.netology.nmedia.utils.Constants.CONTENT_EXTRA
import ru.netology.nmedia.utils.Constants.EXTRA_INPUT_INTENT
import ru.netology.nmedia.utils.Constants.EXTRA_SCREEN_MODE

class NewPostActivityContract : ActivityResultContract<String, String?>() {

    override fun createIntent(context: Context, input: String): Intent {
        return Intent(context, NewPostActivity::class.java)
            .putExtra(EXTRA_INPUT_INTENT, input)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return intent?.getStringExtra(Constants.EXTRA_OUTPUT_INTENT)
    }
}