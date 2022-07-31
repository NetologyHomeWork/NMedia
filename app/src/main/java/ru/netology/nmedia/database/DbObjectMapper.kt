package ru.netology.nmedia.database

import android.database.Cursor
import ru.netology.nmedia.model.Post

class DbObjectMapper {
    fun map(cursor: Cursor): Post {
        with(cursor) {
            return Post(
                id = getLong(getColumnIndexOrThrow(PostColumns.COLUMN_ID)),
                author = getString(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR)),
                content = getString(getColumnIndexOrThrow(PostColumns.COLUMN_CONTENT)),
                published = getString(getColumnIndexOrThrow(PostColumns.COLUMN_PUBLISHED)),
                authorAvatar = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_AUTHOR_AVATAR)),
                isLike = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_IS_LIKE)) != 0,
                shareCount = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_SHARE_COUNT)),
                likesCount = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_LIKE_COUNT)),
                viewsCount = getInt(getColumnIndexOrThrow(PostColumns.COLUMN_VIEWS_COUNT))
            )
        }
    }
}