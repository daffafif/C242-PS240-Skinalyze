package com.capstone.skinaliyze.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.skinaliyze.R
import com.capstone.skinaliyze.ui.main.MainActivity
import com.capstone.skinaliyze.ui.register.RegisterActivity
import androidx.core.widget.ContentLoadingProgressBar

class LoginActivity : AppCompatActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var loadingProgressBar: ContentLoadingProgressBar

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (loginViewModel.isUserLoggedIn()) {
            navigateToMainActivity()
        }

        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        loginButton = findViewById(R.id.btnLogin)
        registerLink = findViewById(R.id.register_link)
        loadingProgressBar = findViewById(R.id.loadingProgressBar)

        loginViewModel.loginStatus.observe(this) { status ->
            loadingProgressBar.visibility = View.GONE

            when (status) {
                "Login Berhasil" -> {
                    Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                }
                "Email belum terdaftar" -> {
                    emailEditText.error = "Email belum terdaftar"
                }
                "Login Gagal: Password salah" -> {
                    passwordEditText.error = "Password salah"
                }
                else -> {
                    Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateInput(email, password)) {
                loadingProgressBar.visibility = View.VISIBLE
                loginViewModel.loginUser(email, password)
            }
        }

        addTextWatchers()

        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (!loginViewModel.validateEmail(email)) {
            emailEditText.error = "Email harus menggunakan domain @gmail.com"
            isValid = false
        } else {
            emailEditText.error = null
        }

        if (!loginViewModel.validatePassword(password)) {
            passwordEditText.error = "Password harus terdiri dari minimal 8 karakter"
            isValid = false
        } else {
            passwordEditText.error = null
        }

        return isValid
    }

    private fun addTextWatchers() {
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!loginViewModel.validateEmail(s.toString())) {
                    emailEditText.error = "Email harus menggunakan domain @gmail.com"
                } else {
                    emailEditText.error = null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length < 8) {
                    passwordEditText.error = "Password harus terdiri dari minimal 8 karakter"
                } else {
                    passwordEditText.error = null
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
