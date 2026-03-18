package cc.darak.aptanywhere.data.repository

import cc.darak.aptanywhere.App
import cc.darak.aptanywhere.data.model.AssetInfo
import cc.darak.aptanywhere.data.model.api.AssetDto
import cc.darak.aptanywhere.data.model.api.toDomain
import cc.darak.aptanywhere.util.PreferencesHelper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class AssetRepository {
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        val apiKey = PreferencesHelper.getApiKey(App.instance)

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .build()

        chain.proceed(newRequest)
    }

    private val retryInterceptor = Interceptor { chain ->
        val request = chain.request()
        var response = chain.proceed(request)

        var tryCount = 0
        val maxLimit = 3

        // Retry on 5xx server errors
        while (!response.isSuccessful && response.code in 500..599 && tryCount < maxLimit) {
            tryCount++
            response.close() // Prevent memory leak

            val waitTime = (500 * tryCount).toLong() // Exponential Backoff
            Thread.sleep(waitTime)

            response = chain.proceed(request)
        }

        response
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(retryInterceptor)
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
    ): List<AssetInfo> {
        // 1. Build params map with only non-null values
        val params = mutableMapOf<String, String>().apply {
            put("number", number)
            complex?.let { put("complex", it) }
        }

        val dtoList = executeRequest<Array<AssetDto>>(
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
        bld: String? = null,
        listingOnly: Boolean = false
    ): List<AssetInfo> {
        // 1. Build params map with only non-null values
        val params = mutableMapOf<String, String>().apply {
            put("keyword", keyword)
            complex?.let { put("complex", it) }
            bld?.let { put("bld", it) }
            put("listing_only", listingOnly.toString())
        }

        // 2. Execute request
        val dtoList = executeRequest<Array<AssetDto>>(
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
    ): List<AssetInfo> {
        // 1. Build params map with only non-null values
        val params = mutableMapOf<String, String>().apply {
            put("complex", complex)
            put("bld", bld)
            unit?.let { put("unit", it) }
        }

        // 2. Execute request
        val dtoList = executeRequest<Array<AssetDto>>(
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

    /**
     * Fetch all available building names for a given complex from the server
     */
    suspend fun fetchBuildingList(complex: String): List<String> {
        val params = mutableMapOf("complex" to complex)

        val result = executeRequest<Array<String>>(
            path = "/api/v1/buildings",
            params = params
        )

        // Sort alphabetically for better UX in Dropdowns
        return result?.toList()?.sortedBy { it.toIntOrNull() ?: 0 } ?: emptyList()
    }

    /**
     * Fetch all available unit names for a given complex and building from the server
     */
    suspend fun fetchUnitList(complex: String, bld: String): List<String> {
        val params = mutableMapOf<String, String>().apply {
            put("complex", complex)
            put("bld", bld)
        }

        val result = executeRequest<Array<String>>(
            path = "/api/v1/units",
            params = params
        )

        // Sort alphabetically for better UX in Dropdowns
        return result?.toList()?.sortedBy { it.toIntOrNull() ?: 0 } ?: emptyList()
    }
}