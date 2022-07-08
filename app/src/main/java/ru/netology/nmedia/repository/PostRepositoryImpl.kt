package ru.netology.nmedia.repository

import androidx.lifecycle.MutableLiveData
import ru.netology.nmedia.model.Post
import ru.netology.nmedia.R

class PostRepositoryImpl : PostRepository {

    private val postList = sortedSetOf<Post>({ o1, o2 -> o2.id.compareTo(o1.id)})

    init {
        postList.addAll(
            listOf(
                Post(
                    id = 9,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Освоение новой профессии — это не только открывающиеся возможности и перспективы, но и настоящий вызов самому себе. Приходится выходить из зоны комфорта и перестраивать привычный образ жизни: менять распорядок дня, искать время для занятий, быть готовым к возможным неудачам в начале пути. В блоге рассказали, как избежать стресса на курсах профпереподготовки → http://netolo.gy/fPD",
                    published = "23 сентября в 10:12",
                    isLike = false,
                    likesCount = 15,
                    shareCount = 8,
                    authorAvatar = R.drawable.ic_netology,
                    viewsCount = 56
                ),
                Post(
                    id = 8,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Делиться впечатлениями о любимых фильмах легко, а что если рассказать так, чтобы все заскучали \uD83D\uDE34\n",
                    published = "22 сентября в 10:14",
                    isLike = false,
                    likesCount = 14,
                    shareCount = 3,
                    authorAvatar = R.drawable.ic_netology,
                    viewsCount = 42
                ),
                Post(
                    id = 7,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Таймбоксинг — отличный способ навести порядок в своём календаре и разобраться с делами, которые долго откладывали на потом. Его главный принцип — на каждое дело заранее выделяется определённый отрезок времени. В это время вы работаете только над одной задачей, не переключаясь на другие. Собрали советы, которые помогут внедрить таймбоксинг \uD83D\uDC47\uD83C\uDFFB",
                    published = "22 сентября в 10:12",
                    isLike = false,
                    likesCount = 26,
                    shareCount = 15,
                    authorAvatar = R.drawable.ic_netology,
                    viewsCount = 89
                ),
                Post(
                    id = 6,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "\uD83D\uDE80 24 сентября стартует новый поток бесплатного курса «Диджитал-старт: первый шаг к востребованной профессии» — за две недели вы попробуете себя в разных профессиях и определите, что подходит именно вам → http://netolo.gy/fQ",
                    published = "21 сентября в 10:12",
                    isLike = false,
                    likesCount = 999,
                    shareCount = 555,
                    authorAvatar = R.drawable.ic_netology,
                    viewsCount = 1_856
                ),
                Post(
                    id = 5,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Диджитал давно стал частью нашей жизни: мы общаемся в социальных сетях и мессенджерах, заказываем еду, такси и оплачиваем счета через приложения.",
                    published = "20 сентября в 10:14",
                    isLike = false,
                    likesCount = 348,
                    shareCount = 123,
                    authorAvatar = R.drawable.ic_netology,
                    viewsCount = 1_125
                ),
                Post(
                    id = 4,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Большая афиша мероприятий осени: конференции, выставки и хакатоны для жителей Москвы, Ульяновска и Новосибирска \uD83D\uDE09",
                    published = "19 сентября в 14:12",
                    isLike = false,
                    likesCount = 724,
                    shareCount = 254,
                    authorAvatar = R.drawable.ic_netology,
                    viewsCount = 1_785
                ),
                Post(
                    id = 3,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Языков программирования много, и выбрать какой-то один бывает нелегко. Собрали подборку статей, которая поможет вам начать, если вы остановили свой выбор на JavaScript.",
                    published = "19 сентября в 10:24",
                    isLike = false,
                    likesCount = 124,
                    shareCount = 88,
                    authorAvatar = R.drawable.ic_netology,
                    viewsCount = 512
                ),
                Post(
                    id = 2,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Знаний хватит на всех: на следующей неделе разбираемся с разработкой мобильных приложений, учимся рассказывать истории и составлять PR-стратегию прямо на бесплатных занятиях \uD83D\uDC47",
                    published = "18 сентября в 10:12",
                    isLike = false,
                    likesCount = 237,
                    shareCount = 69,
                    authorAvatar = R.drawable.ic_netology,
                    viewsCount = 512
                ),
                Post(
                    id = 1,
                    author = "Нетология. Университет интернет-профессий будущего",
                    content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению. Мы растём сами и помогаем расти студентам: от новичков до уверенных профессионалов. Но самое важное остаётся с нами: мы верим, что в каждом уже есть сила, которая заставляет хотеть больше, целиться выше, бежать быстрее. Наша миссия — помочь встать на путь роста и начать цепочку перемен → http://netolo.gy/fyb",
                    published = "21 мая в 18:36",
                    isLike = false,
                    likesCount = 325,
                    shareCount = 147,
                    authorAvatar = R.drawable.ic_netology,
                    viewsCount = 987
                )
            )
        )
    }

    private val data = MutableLiveData(postList.toList())

    override fun getData() = data

    override fun like(post: Post) {
        val newPost = post.copy(
            likesCount = if (!post.isLike) post.likesCount + 1 else post.likesCount - 1,
            isLike = !post.isLike
        )
        postList.remove(post)
        postList.add(newPost)
        updateList()
    }


    override fun share(post: Post) {
        val newPost = post.copy(
            shareCount = post.shareCount + 1
        )
        postList.remove(post)
        postList.add(newPost)
        updateList()
    }

    override fun removeItem(id: Long) {
        postList.remove(findPostById(id))
        updateList()
    }

    override fun savePost(post: Post) {
        if (post.id == 0L) {
            val newPost = post.copy(id = postList.first().id + 1)
            postList.add(newPost)
        } else {
            val newPost = findPostById(post.id)?.copy(content = post.content) ?: return
            postList.remove(post)
            postList.add(newPost)
        }
        updateList()
    }

    private fun updateList() {
        data.value = postList.toList()
    }

    private fun findPostById(id: Long): Post? {
        return postList.find { it.id == id }
    }
}