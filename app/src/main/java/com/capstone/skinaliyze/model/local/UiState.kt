package com.capstone.skinaliyze.model.local

sealed class UiState<out R> {
    data class Success<out T>(val data: T) : UiState<T>()
    data class Error(val error: String) : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
}