package cc.darak.aptanywhere.util

fun formatPhoneNumber(number: String): String {
    // 1. Remove all non-numeric characters
    val digits = number.replace(Regex("\\D"), "")

    return when {
        // Case: 02-xxxx-xxxx or 02-xxx-xxxx
        digits.startsWith("02") -> {
            when (digits.length) {
                9 -> digits.replace(Regex("(\\d{2})(\\d{3})(\\d{4})"), "$1-$2-$3")
                10 -> digits.replace(Regex("(\\d{2})(\\d{4})(\\d{4})"), "$1-$2-$3")
                else -> digits
            }
        }
        // Case: 010-xxxx-xxxx or 0xx-xxx-xxxx
        digits.startsWith("0") -> {
            when (digits.length) {
                10 -> digits.replace(Regex("(\\d{3})(\\d{3})(\\d{4})"), "$1-$2-$3")
                11 -> digits.replace(Regex("(\\d{3})(\\d{4})(\\d{4})"), "$1-$2-$3")
                else -> digits
            }
        }
        // Case: xxxx-xxxx or xxx-xxxx (No area code)
        else -> {
            when (digits.length) {
                7 -> digits.replace(Regex("(\\d{3})(\\d{4})"), "$1-$2")
                8 -> digits.replace(Regex("(\\d{4})(\\d{4})"), "$1-$2")
                else -> digits
            }
        }
    }
}