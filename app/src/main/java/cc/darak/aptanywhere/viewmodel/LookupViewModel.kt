package cc.darak.aptanywhere.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import cc.darak.aptanywhere.data.model.AssetInfo
import cc.darak.aptanywhere.data.model.SearchType
import cc.darak.aptanywhere.data.repository.AssetRepository

class LookupViewModel() : ViewModel() {

    val repository = AssetRepository()

    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var complexList by mutableStateOf<List<String>>(emptyList())
        private set
    var searchResults = mutableStateOf<List<AssetInfo>?>(null)

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                complexList = repository.fetchComplexList()
                // If successful, loading ends and overlay disappears
                isLoading = false
            } catch (e: Exception) {
                // If failed, keep the overlay but show error message
                errorMessage = e.message
                isLoading = false
            }
        }
    }

    fun performSearch(
        type: SearchType,
        phone: String?,
        keyword: String?,
        complex: String?,
        bld: String?,
        unit: String?
    ) {
        // 1. Pre-process: Treat blank strings as
        val cPhone = phone?.takeIf { it.isNotBlank() }
        val cKeyword = keyword?.takeIf { it.isNotBlank() }
        val cComplex = complex?.takeIf { it.isNotBlank() }
        val cBld = bld?.takeIf { it.isNotBlank() }
        val cUnit = unit?.takeIf { it.isNotBlank() }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null // Clear previous errors

            try {
                when (type) {
                    SearchType.PHONE -> {
                        val targetPhone = cPhone ?: throw Exception("전화번호를 입력해주세요.")
                        searchResults.value = repository.fetchInfoByNumber(targetPhone, cComplex)
                    }
                    SearchType.KEYWORD -> {
                        val targetKeyword = cKeyword ?: throw Exception("키워드를 입력해주세요.")
                        searchResults.value = repository.searchByKeyword(targetKeyword, cComplex, cBld)
                    }
                    SearchType.UNIT -> {
                        // Unit search requires both complex and building
                        val targetComplex = cComplex ?: throw Exception("단지를 선택해주세요.")
                        val targetBld = cBld ?: throw Exception("동 번호를 입력해주세요.")
                        searchResults.value = repository.searchByUnit(targetComplex, targetBld, cUnit)
                    }
                }
            } catch (e: Exception) {
                // Handle error (e.g., Network timeout, API error)
                errorMessage = e.message ?: "알 수 없는 오류가 발생했습니다."
            } finally {
                isLoading = false
            }
        }
    }

    fun isInputValid(
        type: SearchType,
        phone: String,
        keyword: String,
        complex: String?,
        bld: String
    ): Boolean {
        return when (type) {
            SearchType.PHONE -> phone.trim().length >= 4

            SearchType.KEYWORD -> keyword.trim().isNotBlank()

            SearchType.UNIT -> complex != null && bld.trim().isNotBlank()
        }
    }

    // Reset results after navigation to prevent re-triggering
    fun consumeResults() {
        searchResults.value = null
    }
}