package com.example.floramarket.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.floramarket.model.User

class UserViewModel : ViewModel() {

    var user by mutableStateOf(User())
        private set

    var nameInput by mutableStateOf("")
        private set

    var emailInput by mutableStateOf("")
        private set

    var phoneInput by mutableStateOf("")
        private set

    var registrationError by mutableStateOf<String?>(null)
        private set

    fun updateNameInput(value: String) {
        nameInput = value
        registrationError = null
    }

    fun updateEmailInput(value: String) {
        emailInput = value
        registrationError = null
    }

    fun updatePhoneInput(value: String) {
        if (value.isEmpty() || value.matches(Regex("^[\\d\\s+\\-()]*$"))) {
            phoneInput = value
            registrationError = null
        }
    }

    fun register(): Boolean {
        when {
            nameInput.isBlank() -> {
                registrationError = "Введите имя"
                return false
            }
            emailInput.isBlank() || !emailInput.contains("@") -> {
                registrationError = "Введите корректный email"
                return false
            }
            phoneInput.isBlank() -> {
                registrationError = "Введите номер телефона"
                return false
            }
        }

        user = User(
            name = nameInput,
            email = emailInput,
            phone = phoneInput,
            isRegistered = true
        )

        return true
    }

    fun logout() {
        user = User()
        nameInput = ""
        emailInput = ""
        phoneInput = ""
        registrationError = null
    }

    fun clearError() {
        registrationError = null
    }
}