package org.calamarfederal.messyink.common.compose


/**
 * Convert to [String], but if it can be shown as an [Long], show that instead
 */
fun Double.toStringAllowShorten(): String {
    val l = toLong()
    return if (this == l.toDouble())
        l.toString()
    else
        toString()
}

/**
 * Convert to [String], but if it can be shown as an [Int], show that instead
 */
fun Float.toStringAllowShorten(): String {
    val l = toInt()
    return if (this == l.toFloat())
        l.toString()
    else
        toString()
}
