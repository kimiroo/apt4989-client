package cc.darak.aptanywhere.util

import cc.darak.aptanywhere.data.model.PropertyInfo

/**
 * Utility functions for property data processing
 */
object PropertyUtils {

    /**
     * Sanitizes a string by removing all non-digit characters
     */
    private fun String.sanitizePhoneNumber(): String {
        return this.replace(Regex("[^0-9]"), "")
    }

    /**
     * Checks if the given [searchNumber] exists in the [targetString] (multi-line)
     */
    private fun isNumberInString(searchNumber: String, targetString: String?): Boolean {
        if (targetString.isNullOrBlank()) return false

        val cleanSearch = searchNumber.sanitizePhoneNumber()
        if (cleanSearch.isEmpty()) return false

        // Split by lines and compare each sanitized number
        return targetString.lineSequence()
            .map { it.sanitizePhoneNumber() }
            .any { it == cleanSearch }
    }

    /**
     * Identifies if the number belongs to the owner
     */
    fun isOwner(info: PropertyInfo, number: String): Boolean {
        return isNumberInString(number, info.ownerNumber)
    }

    /**
     * Identifies if the number belongs to the tenant
     */
    fun isTenant(info: PropertyInfo, number: String): Boolean {
        return isNumberInString(number, info.tenantNumber)
    }
}