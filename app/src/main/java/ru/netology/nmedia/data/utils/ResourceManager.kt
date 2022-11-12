package ru.netology.nmedia.data.utils

import android.content.Context
import androidx.annotation.StringRes

class ResourceManager(private val context: Context) {

    fun getString(@StringRes resId: Int): String = context.getString(resId)
    fun getString(@StringRes resId: Int, vararg formatArgs: Any?): String =
        context.getString(resId, *formatArgs)
}