package ru.netology.nmedia.utils

object Constants {

    const val EXTRA_OUTPUT_INTENT = "output_intent"
    const val EXTRA_INPUT_INTENT = "input_intent"
    const val EXTRA_SCREEN_MODE = "screen_mode"
    const val EDIT_MODE = "edit_mode"
    const val ADD_MODE  ="add_mode"
    const val UNKNOWN_MODE = "unknown_mode"
    const val CONTENT_EXTRA = "content_extra"
    const val PATTERN_FOR_YOUTUBE_FIND = ("(?:^|[\\W])((ht|f)tp(s?):/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]*$~@!:/{};']*)")
    const val KEY_POST = "key_post"
    const val FILE_NAME = "post.json"
}