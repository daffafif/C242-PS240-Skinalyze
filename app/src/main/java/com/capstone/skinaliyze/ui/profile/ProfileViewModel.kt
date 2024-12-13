package com.capstone.skinaliyze.ui.profile

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference
    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> = _userProfile

    private val sharedPreferences: SharedPreferences =
        application.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

    fun getUserProfile() {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userRef = database.child("users").child(user.uid)
            userRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val username = snapshot.child("username").value.toString()
                    val age = snapshot.child("age").value.toString().toIntOrNull() ?: 0
                    val gender = snapshot.child("gender").value.toString()
                    val profilePicture = snapshot.child("profilePicture").value.toString()
                    _userProfile.value = UserProfile(username, age, gender, profilePicture)
                }
            }
        }
    }

    fun updateUserProfile(userProfile: UserProfile) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val userRef = database.child("users").child(user.uid)
            val updatedProfileData = mapOf(
                "username" to userProfile.username,
                "age" to userProfile.age,
                "gender" to userProfile.gender,
                "profilePicture" to userProfile.profilePicture
            )
            userRef.updateChildren(updatedProfileData)
        }
    }

    fun encodeImageToBase64(image: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun saveLoginStatus(isLoggedIn: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }
}
