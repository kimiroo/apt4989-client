package cc.darak.aptanywhere.util

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit
import cc.darak.aptanywhere.data.model.PrefKeys

object PreferencesHelper {
    private const val PREF_NAME = "secret_shared_prefs"

    private fun getEncryptedPrefs(context: Context): SharedPreferences {
        val mainKey = MasterKey.Builder(context.applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        // Use the constructor-like create method with MasterKey object
        return EncryptedSharedPreferences.create(
            context.applicationContext,
            PREF_NAME,
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveApiSettings(context: Context, url: String, key: String? = null) {
        val cleanUrl = url.trim().removeSuffix("/")
        val cleanKey = key?.trim()

        getEncryptedPrefs(context).edit().apply {
            putString(PrefKeys.API_URL, cleanUrl)
            putBoolean(PrefKeys.IS_SETUP_COMPLETE, true)
            if (!cleanKey.isNullOrBlank()) {
                putString(PrefKeys.API_KEY, cleanKey)
            }
            apply()
        }
    }

    fun saveOverlaySettings(context: Context, showOverlay: Boolean, yOffset: Int, maxHeight: Int) {
        getEncryptedPrefs(context).edit().apply {
            putBoolean(PrefKeys.SHOW_OVERLAY, showOverlay)
            putInt(PrefKeys.OVERLAY_Y_OFFSET, yOffset)
            putInt(PrefKeys.MAX_OVERLAY_HEIGHT, maxHeight)
            apply()
        }
    }

    fun getApiUrl(context: Context): String =
        getEncryptedPrefs(context).getString(PrefKeys.API_URL, "") ?: ""

    fun getApiKey(context: Context): String =
        getEncryptedPrefs(context).getString(PrefKeys.API_KEY, "") ?: ""

    fun getShowOverlay(context: Context): Boolean =
        getEncryptedPrefs(context).getBoolean(PrefKeys.SHOW_OVERLAY, true)

    fun getOverlayYOffset(context: Context): Int =
        getEncryptedPrefs(context).getInt(PrefKeys.OVERLAY_Y_OFFSET, 500)

    fun getMaxOverlayMaxHeight(context: Context): Int =
        getEncryptedPrefs(context).getInt(PrefKeys.MAX_OVERLAY_HEIGHT, 400)

    fun isSetupComplete(context: Context): Boolean =
        getEncryptedPrefs(context).getBoolean(PrefKeys.IS_SETUP_COMPLETE, false)
}