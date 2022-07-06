package ru.netology.nmedia

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

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
