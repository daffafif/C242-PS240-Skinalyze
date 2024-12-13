package com.capstone.skinaliyze.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.skinaliyze.databinding.ActivityRegisterBinding
import com.capstone.skinaliyze.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBinding()
        implementAnimations()
        addTextWatchers()

        binding.registerButton.setOnClickListener {
            val username = binding.usernameLayout.text.toString().trim()
            val email = binding.emailLayout.text.toString().trim()
            val password = binding.passwordLayout.text.toString().trim()

            if (viewModel.validateCredentials(username, email, password)) {
                binding.loadingProgressBar.visibility = View.VISIBLE
                viewModel.registerUser(this, email, password, username)
            } else {
                Toast.makeText(this, "Username, Email, atau Password tidak valid", Toast.LENGTH_SHORT).show()
            }
        }

        binding.loginLink.setOnClickListener {
            navigateToLogin()
        }

        // Observing registration status
        viewModel.registrationResult.observe(this) { result ->
            binding.loadingProgressBar.visibility = View.GONE
            when (result) {
                is RegistrationResult.Success -> {
                    Toast.makeText(this, "Registrasi Berhasil", Toast.LENGTH_SHORT).show()
                    clearFields()
                    navigateToLogin()
                }
                is RegistrationResult.Failure -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupBinding() {}

    private fun implementAnimations() {
        with(binding) {
            slideInFromLeft(tvRegisterTitle, 1000)
            slideInFromLeft(tvRegisterDescription, 1400)
            fadeInAnimation(usernameLayout, 1800)
            fadeInAnimation(emailLayout, 2000)
            fadeInAnimation(passwordLayout, 2000)
            fadeInAnimation(registerButton, 1400)
        }
    }

    private fun addTextWatchers() {
        binding.usernameLayout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.usernameLayout.error = if (s.toString().isEmpty()) "Username tidak boleh kosong" else null
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.emailLayout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.emailLayout.error = if (!s.toString().endsWith("@gmail.com")) "Email harus menggunakan domain @gmail.com" else null
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.passwordLayout.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.passwordLayout.error = if (s.toString().length < 8) "Kata sandi harus terdiri dari setidaknya 8 karakter" else null
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fadeInAnimation(view: View, duration: Long = 1000) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .start()
    }

    private fun slideInFromLeft(view: View, duration: Long = 1000) {
        view.translationX = -500f
        view.visibility = View.VISIBLE
        view.animate()
            .translationX(0f)
            .setDuration(duration)
            .start()
    }

    private fun clearFields() {
        binding.usernameLayout.text = null
        binding.emailLayout.text = null
        binding.passwordLayout.text = null
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
