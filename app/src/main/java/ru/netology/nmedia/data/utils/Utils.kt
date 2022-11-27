package ru.netology.nmedia.data.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import okio.IOException
import ru.netology.nmedia.R
import ru.netology.nmedia.data.AppException
import ru.netology.nmedia.data.utils.Constants.PATTERN_FOR_YOUTUBE_FIND
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.regex.Pattern

private val DELIMITER = BigDecimal.valueOf(1_000)
private const val DOUBLE_DELIMITER = 1_000.0
private val MILLION_DELIMITER = BigDecimal.valueOf(1_000_000)
private const val DOUBLE_MILLION_DELIMITER = 1_000_000.0

fun formatCount(count: Int) =
    when (count) {
        in 0..999 -> count.toString()
        in 1_000..999_999 -> "${formatNumber(count, DOUBLE_DELIMITER, DELIMITER)}K"
        else -> "${formatNumber(count, DOUBLE_MILLION_DELIMITER, MILLION_DELIMITER)}M"
    }

private fun formatNumber(count: Int, delimiter: Double, bigDecimalDelimiter: BigDecimal): String {
    val decimal = BigDecimal(count).divide(bigDecimalDelimiter)
    return if (decimal.scale() == 1) (count / delimiter).toString()
    else {
        val s = decimal.setScale(1, RoundingMode.DOWN)
        val stringDecimal = DecimalFormat("#.#").format(s)
        return if (stringDecimal.contains(".") && stringDecimal.last() == '0') {
            stringDecimal.dropLast(1)
        }
        else stringDecimal
    }
}

fun View.showKeyboard() {
    getInputMethodManager(this).showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    getInputMethodManager(this).hideSoftInputFromWindow(this.windowToken, 0)
}

fun parsingUrlLink(content: String): String {
    val urlPattern: Pattern = Pattern.compile(
        PATTERN_FOR_YOUTUBE_FIND,
        Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
    )
    val urlList = mutableListOf<String>()
    val urlMatcher = urlPattern.matcher(content)
    var matchStart: Int
    var matchEnd: Int


    while (urlMatcher.find()) {
        matchStart = urlMatcher.start(1)
        matchEnd = urlMatcher.end()

        val url = content.substring(matchStart, matchEnd)
        urlList.add(url)
    }
    return urlList.first { it.contains("youtube") or it.contains("youtu.be") }
}

internal inline fun<T> wrapException(resourceManager: ResourceManager, block: () -> T): T {
    try {
        return block.invoke()
    } catch (e: Exception) {
        when(e) {
            is AppException.ApiError -> throw e
            is IOException -> {
                throw AppException.NetworkError(resourceManager.getString(R.string.error_connection))
            }
            else -> {
                throw AppException.UnknownError(resourceManager.getString(R.string.unknown_error))
            }
        }
    }
}

private fun getInputMethodManager(view: View): InputMethodManager {
    val context = view.context
    return context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
}

