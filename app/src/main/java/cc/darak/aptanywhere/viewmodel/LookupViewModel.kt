package cc.darak.aptanywhere.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.darak.aptanywhere.data.repository.PropertyRepository.fetchComplexList
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class LookupViewModel : ViewModel() {
    var isLoading by mutableStateOf(true)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var complexList by mutableStateOf<List<String>>(emptyList())
        private set

    var selectedComplex by mutableStateOf<String?>(null)
        private set

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                complexList = fetchComplexList()
                // If successful, loading ends and overlay disappears
                isLoading = false
            } catch (e: Exception) {
                // If failed, keep the overlay but show error message
                errorMessage = e.message
                isLoading = false
            }
        }
    }

    fun updateComplex(value: String?) {
        selectedComplex = value
    }
}