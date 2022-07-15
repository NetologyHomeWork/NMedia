package ru.netology.nmedia.activity

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.utils.Constants

class NewPostActivityContract : ActivityResultContract<Unit, String?>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(context, NewPostActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        return intent?.getStringExtra(Constants.EXTRA_POST_INTENT)
    }

}