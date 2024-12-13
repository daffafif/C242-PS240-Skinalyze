package com.capstone.skinaliyze.ui.register

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    private val _registrationResult = MutableLiveData<RegistrationResult>()
    val registrationResult: LiveData<RegistrationResult> get() = _registrationResult

    fun validateCredentials(username: String, email: String, password: String): Boolean {
        return username.isNotEmpty() && email.endsWith("@gmail.com") && password.length >= 8
    }

    fun registerUser(context: Context, email: String, password: String, username: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        val user = User(username, email, password)
                        saveUserToDatabase(it, user)
                    }
                    _registrationResult.value = RegistrationResult.Success
                } else {
                    _registrationResult.value = RegistrationResult.Failure(task.exception?.message ?: "Registrasi gagal")
                }
            }
            .addOnFailureListener { exception ->
                _registrationResult.value = RegistrationResult.Failure("Terjadi kesalahan: ${exception.message}")
            }
    }

    private fun saveUserToDatabase(userId: String, user: User) {
        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                // Berhasil menyimpan data ke Realtime Database
            }
            .addOnFailureListener { exception ->
                _registrationResult.value = RegistrationResult.Failure("Gagal menyimpan data pengguna: ${exception.message}")
            }
    }

    data class User(val username: String, val email: String, val password: String)
}
