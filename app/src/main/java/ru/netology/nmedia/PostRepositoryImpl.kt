package ru.netology.nmedia

import androidx.lifecycle.MutableLiveData

class PostRepositoryImpl : PostRepository {

    private val post = Post(
        author = "Нетология. Университет интернет-профессий будущего",
        published = "21 мая в 18:36",
        content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
        authorAvatar = R.drawable.ic_netology,
        shareCount = 5,
        likesCount = 10,
        viewsCount = 50
    )

    private val data = MutableLiveData(post)

    override fun getData() = data

    override fun like() {
        data.value = data.value?.let {
            it.copy(
                likesCount = if (!it.isLike) it.likesCount + 1 else it.likesCount - 1,
                isLike = !it.isLike
            )
        }
    }

    override fun share() {
        data.value = data.value?.let {
            it.copy(
                shareCount = it.shareCount + 1
            )
        }
    }
}