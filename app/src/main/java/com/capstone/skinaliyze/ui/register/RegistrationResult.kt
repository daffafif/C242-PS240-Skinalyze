package com.capstone.skinaliyze.ui.register

sealed class RegistrationResult {
    object Success : RegistrationResult()
    data class Failure(val errorMessage: String) : RegistrationResult()
}
