package cc.darak.aptanywhere.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import cc.darak.aptanywhere.R
import cc.darak.aptanywhere.data.model.AssetInfo
import cc.darak.aptanywhere.data.model.SearchType
import cc.darak.aptanywhere.data.repository.AssetRepository

class LookupViewModel() : ViewModel() {

    val repository = AssetRepository()

    // For dynamically loading "loading messages"
    var loadingResId by mutableIntStateOf(R.string.loading_default)
        private set
    var loadingArgs by mutableStateOf<String?>(null)
        private set

    // State variables
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var complexList by mutableStateOf<List<String>>(emptyList())
        private set
    var buildingList by mutableStateOf<List<String>>(emptyList())
        private set
    var searchResults = mutableStateOf<List<AssetInfo>?>(null)

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            loadingResId = R.string.loading_complex_list
            loadingArgs = null
            isLoading = true
            errorMessage = null
            try {
                complexList = repository.fetchComplexList()
            } catch (e: Exception) {
                // If failed, keep the overlay but show error message
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }
    }

    fun onComplexChanged(complex: String?) {
        if (complex.isNullOrBlank()) {
            buildingList = emptyList()
            return
        }

        viewModelScope.launch {
            loadingResId = R.string.loading_building_list
            loadingArgs = complex
            isLoading = true
            errorMessage = null
            try {
                buildingList = repository.fetchBuildingList(complex)
            } catch (e: Exception) {
                errorMessage = e.message
                buildingList = emptyList()
            } finally {
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
            loadingResId = R.string.loading_search
            loadingArgs = null
            isLoading = true
            errorMessage = null // Clear previous errors

            try {
                when (type) {
                    SearchType.PHONE -> {
                        val targetPhone = cPhone ?: throw Exception("ERROR: Phone number empty")
                        searchResults.value = repository.fetchInfoByNumber(targetPhone, cComplex)
                    }
                    SearchType.KEYWORD -> {
                        val targetKeyword = cKeyword ?: throw Exception("ERROR: Keyword empty")
                        searchResults.value = repository.searchByKeyword(targetKeyword, cComplex, cBld)
                    }
                    SearchType.UNIT -> {
                        // Unit search requires both complex and building
                        val targetComplex = cComplex ?: throw Exception("ERROR: Complex empty")
                        val targetBld = cBld ?: throw Exception("ERROR: Bld empty")
                        searchResults.value = repository.searchByUnit(targetComplex, targetBld, cUnit)
                    }
                }
            } catch (e: Exception) {
                // Handle error (e.g., Network timeout, API error)
                errorMessage = e.message ?: "ERROR: Unknown error"
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