package com.capstone.skinaliyze.ui.login

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    val loginStatus = MutableLiveData<String>()

    fun loginUser(email: String, password: String) {
        if (validateEmail(email) && validatePassword(password)) {
            loginStatus.value = "Loading..."

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        loginStatus.value = if (user != null) {
                            // Menyimpan status login
                            saveLoginStatus(true)
                            "Login Berhasil"
                        } else {
                            "Login Gagal"
                        }
                    } else {
                        loginStatus.value = "Login Gagal: ${task.exception?.message}"
                    }
                }
                .addOnFailureListener { exception ->
                    loginStatus.value = "Terjadi kesalahan: ${exception.message}"
                }
        } else {
            loginStatus.value = "Email harus menggunakan @gmail.com dan password minimal 8 karakter"
        }
    }

    fun validateEmail(email: String): Boolean {
        return email.contains("@gmail.com")
    }

    fun validatePassword(password: String): Boolean {
        return password.length >= 8
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    fun isUserLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
}
