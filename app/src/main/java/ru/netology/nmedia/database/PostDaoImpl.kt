package ru.netology.nmedia.database

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import ru.netology.nmedia.model.Post

class PostDaoImpl(
    private val db: SQLiteDatabase
) : PostDao {

    private val dbMapper = DbObjectMapper()

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            null,
            null,
            null,
            null,
            "${PostColumns.COLUMN_ID} DESC"
        ).use {
            while (it.moveToNext()) {
                posts.add(dbMapper.map(it))
            }
        }
        return posts
    }

    override fun save(post: Post): Post {
        val values = ContentValues().apply {
            if (post.id != 0L) {
                put(PostColumns.COLUMN_ID, post.id)
            }
            put(PostColumns.COLUMN_AUTHOR, post.author)
            put(PostColumns.COLUMN_CONTENT, post.content)
            put(PostColumns.COLUMN_PUBLISHED, post.published)
            put(PostColumns.COLUMN_AUTHOR_AVATAR, post.authorAvatar)
            if (post.isLike) {
                put(PostColumns.COLUMN_IS_LIKE, PostColumns.LIKE_TRUE)
            } else {
                put(PostColumns.COLUMN_IS_LIKE, PostColumns.LIKE_FALSE)
            }
            put(PostColumns.COLUMN_LIKE_COUNT, post.likesCount)
            put(PostColumns.COLUMN_SHARE_COUNT, post.shareCount)
            put(PostColumns.COLUMN_VIEWS_COUNT, post.viewsCount)
        }
        val id = db.replace(PostColumns.TABLE, null, values)
        db.query(
            PostColumns.TABLE,
            PostColumns.ALL_COLUMNS,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null,
            null,
            null
        ).use {
            it.moveToNext()
            return dbMapper.map(it)
        }
    }

    override fun like(post: Post) {
        val values = ContentValues().apply {
            if (post.isLike) {
                put(PostColumns.COLUMN_IS_LIKE, PostColumns.LIKE_TRUE)
            } else {
                put(PostColumns.COLUMN_IS_LIKE, PostColumns.LIKE_FALSE)
            }
            put(PostColumns.COLUMN_LIKE_COUNT, post.likesCount)
        }
        db.update(
            PostColumns.TABLE,
            values,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(post.id.toString())
        )
    }

    override fun removeById(postId: Long) {
        db.delete(
            PostColumns.TABLE,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(postId.toString())
        )
    }

    override fun share(post: Post) {
        val values = ContentValues().apply {
            put(PostColumns.COLUMN_SHARE_COUNT, post.shareCount)
        }
        db.update(
            PostColumns.TABLE,
            values,
            "${PostColumns.COLUMN_ID} = ?",
            arrayOf(post.id.toString())
        )
    }

    companion object {
        const val DDLs = "CREATE TABLE posts_table(\n" +
                "\tpost_id \t\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tauthor\t\t\tTEXT NOT NULL,\n" +
                "\tcontent\t\t\tTEXT NOT NULL,\n" +
                "\tpublished\t\tTEXT NOT NULL,\n" +
                "\tauthor_avatar\tINTEGER,\n" +
                "\tis_like\t\t\tBOOLEAN NOT NULL DEFAULT false,\n" +
                "\tshare_count\t\tINTEGER DEFAULT 0,\n" +
                "\tlike_count\t\tINTEGER DEFAULT 0,\n" +
                "\tviews_count\t\tINTEGER DEFAULT 0\n" +
                ");"
    }
}