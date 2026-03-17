package cc.darak.aptanywhere.data.repository

import android.util.Log
import cc.darak.aptanywhere.App
import cc.darak.aptanywhere.data.model.PropertyInfo
import cc.darak.aptanywhere.data.model.api.PropertyDto
import cc.darak.aptanywhere.data.model.api.toDomain
import cc.darak.aptanywhere.util.PreferencesHelper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request

object PropertyRepository {
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // Retrieve the API Key from your helper (Use a placeholder or dummy context carefully)
        // Since Repository is a Singleton, you'll need to pass the applicationContext once or access it globally.
        val apiKey = PreferencesHelper.getApiKey(App.instance)

        val newRequest = originalRequest.newBuilder()
            .header("X-API-KEY", apiKey) // Use your server's expected header name
            .build()

        chain.proceed(newRequest)
    }
    private val client = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .build()
    private val gson = Gson()

    /**
     * 공통 네트워크 요청 및 JSON 파싱 함수
     * reified T를 사용해 별도의 파서 람다 없이도 자동 파싱 가능
     */
    private suspend inline fun <reified T> executeRequest(
        path: String,
        params: Map<String, String> = emptyMap()
    ): T? = withContext(Dispatchers.IO) {
        val url = PreferencesHelper.getApiUrl(App.instance)
        val urlBuilder = "$url$path".toHttpUrlOrNull()?.newBuilder() ?: return@withContext null
        params.forEach { (key, value) -> urlBuilder.addQueryParameter(key, value) }

        val request = Request.Builder().url(urlBuilder.build()).build()

        client.newCall(request).execute().use { response ->
            val bodyString = response.body.string()
            // Gson을 이용해 JSON 문자열을 원하는 객체(T)로 즉시 변환
            gson.fromJson(bodyString, T::class.java)
        }
    }

    /**
     * Phone number lookup with optional complex filter
     */
    suspend fun fetchInfoByNumber(
        number: String,
        complex: String? = null
    ): List<PropertyInfo> {
        val params = mutableMapOf("number" to number)
        complex?.let { params["complex"] = it }

        val dtoList = executeRequest<Array<PropertyDto>>(
            path = "/api/v1/lookup/phone",
            params = params
        )

        return dtoList?.map { it.toDomain() } ?: emptyList()
    }

    /**
     * Keyword search with optional filters (complex, bld)
     */
    suspend fun searchByKeyword(
        keyword: String,
        complex: String? = null,
        bld: String? = null
    ): List<PropertyInfo> {
        // 1. Build params map with only non-null values
        val params = mutableMapOf("keyword" to keyword)
        complex?.let { params["complex"] = it }
        bld?.let { params["bld"] = it }

        // 2. Execute request
        val dtoList = executeRequest<Array<PropertyDto>>(
            path = "/api/v1/lookup/keyword",
            params = params
        )

        return dtoList?.map { it.toDomain() } ?: emptyList()
    }

    /**
     * Unit search with optional filters (unit)
     */
    suspend fun searchByUnit(
        complex: String,
        bld: String,
        unit: String? = null
    ): List<PropertyInfo> {
        // 1. Build params map with only non-null values
        val params = mutableMapOf("complex" to complex)
        params["bld"] = bld
        unit?.let { params["unit"] = it }

        // 2. Execute request
        val dtoList = executeRequest<Array<PropertyDto>>(
            path = "/api/v1/lookup/unit",
            params = params
        )

        return dtoList?.map { it.toDomain() } ?: emptyList()
    }

    /**
     * Fetch all available complex names from the server
     */
    suspend fun fetchComplexList(): List<String> {
        val result = executeRequest<Array<String>>(
            path = "/api/v1/complexes"
        )

        // Sort alphabetically for better UX in Dropdowns
        return result?.toList()?.sorted() ?: emptyList()
    }
}