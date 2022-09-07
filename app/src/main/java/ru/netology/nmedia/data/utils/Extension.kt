package ru.netology.nmedia.data.utils

import okhttp3.OkHttpClient
import okhttp3.Request

fun OkHttpClient.execute(request: Request) {
    this.newCall(request)
        .execute()
        .close()
}