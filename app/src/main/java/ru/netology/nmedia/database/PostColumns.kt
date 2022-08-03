package ru.netology.nmedia.database

object PostColumns {
    const val TABLE = "posts_table"
    const val COLUMN_ID = "post_id"
    const val COLUMN_AUTHOR = "author"
    const val COLUMN_CONTENT = "content"
    const val COLUMN_PUBLISHED = "published"
    const val COLUMN_IS_LIKE = "is_like"
    const val COLUMN_SHARE_COUNT = "share_count"
    const val COLUMN_LIKE_COUNT = "like_count"
    const val COLUMN_VIEWS_COUNT = "views_count"
    const val COLUMN_AUTHOR_AVATAR = "author_avatar"
    val ALL_COLUMNS = arrayOf(
        COLUMN_ID,
        COLUMN_AUTHOR,
        COLUMN_CONTENT,
        COLUMN_PUBLISHED,
        COLUMN_IS_LIKE,
        COLUMN_SHARE_COUNT,
        COLUMN_LIKE_COUNT,
        COLUMN_VIEWS_COUNT,
        COLUMN_AUTHOR_AVATAR
    )
    const val LIKE_TRUE = 1
    const val LIKE_FALSE = 0
}