package ru.netology.nmedia.utils

object Constants {

    const val PATTERN_FOR_YOUTUBE_FIND = ("(?:^|[\\W])((ht|f)tp(s?):/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]*$~@!:/{};']*)")
    const val FILE_NAME = "post.json"
}