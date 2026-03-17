package cc.darak.aptanywhere.data.model

sealed class UiState {
    object Loading : UiState()
    object NotFound : UiState()
    data class Success(val number: String, val infoList: List<PropertyInfo>) : UiState()
    data class Error(val message: String) : UiState()
}