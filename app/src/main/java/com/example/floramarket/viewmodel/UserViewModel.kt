package com.example.floramarket.viewmodel

import android.app.Application
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.floramarket.data.AppDatabase
import com.example.floramarket.model.User
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

    var user by mutableStateOf(User())
        private set

    var isEditing by mutableStateOf(false)
        private set

    var nameInput by mutableStateOf("")
        private set
    var emailInput by mutableStateOf("")
        private set
    var phoneInput by mutableStateOf("")
        private set
    var avatarUrl by mutableStateOf("")
        private set
    var registrationError by mutableStateOf<String?>(null)
        private set

    var isProfileLoading by mutableStateOf(true)
        private set
    var userExists by mutableStateOf(false)
        private set

    var showExistingAccountDialog by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            userDao.getActiveUser().collectLatest { activeUser ->
                if (activeUser != null) {
                    user = activeUser
                    nameInput = activeUser.name
                    emailInput = activeUser.email
                    phoneInput = activeUser.phone
                    avatarUrl = activeUser.avatarUrl
                    userExists = true
                } else {
                    userExists = false
                }
                isProfileLoading = false
            }
        }
    }

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

    fun updateAvatarUrl(uri: Uri) {
        avatarUrl = uri.toString()
    }

    fun startEditing() {
        isEditing = true
    }

    fun cancelEditing() {
        nameInput = user.name
        emailInput = user.email
        phoneInput = user.phone
        avatarUrl = user.avatarUrl
        isEditing = false
        registrationError = null
    }

    fun logout() {
        viewModelScope.launch {
            userDao.logoutAllUsers()
            user = User()
            nameInput = ""
            emailInput = ""
            phoneInput = ""
            avatarUrl = ""
            isEditing = false
            userExists = false
        }
    }

    private fun validateInput(): Boolean {
        return when {
            nameInput.isBlank() -> {
                registrationError = "Введите имя"; false
            }
            nameInput.length < 2 -> {
                registrationError = "Имя должно содержать минимум 2 символа"; false
            }
            emailInput.isBlank() -> {
                registrationError = "Введите email"; false
            }
            !emailInput.contains("@") || !emailInput.contains(".") -> {
                registrationError = "Введите корректный email"; false
            }
            phoneInput.filter { it.isDigit() }.length < 10 -> {
                registrationError = "Введите корректный телефон (минимум 10 цифр)"; false
            }
            else -> true
        }
    }

    fun onProfileSubmit() {
        if (!validateInput()) return

        viewModelScope.launch {
            val existingUser = userDao.getUserByEmail(emailInput.trim())

            if (existingUser != null && !userExists) {
                // Пользователь с таким email уже есть в базе — диалог
                showExistingAccountDialog = true
            } else {
                // Новый пользователь или редактирование
                performSave()
            }
        }
    }

    fun confirmExistingAccount() {
        showExistingAccountDialog = false
        // Входим в существующий аккаунт
        viewModelScope.launch {
            val existingUser = userDao.getUserByEmail(emailInput.trim())
            if (existingUser != null) {

                // Активируем пользователя
                val updatedUser = existingUser.copy(
                    isRegistered = true
                )
                userDao.updateUser(updatedUser)

                user = updatedUser
                nameInput = updatedUser.name
                emailInput = updatedUser.email
                phoneInput = updatedUser.phone
                avatarUrl = updatedUser.avatarUrl
                userExists = true
            }
        }
    }

    fun cancelExistingAccount() {
        showExistingAccountDialog = false
    }

    private fun performSave() {
        viewModelScope.launch {
            val cleanPhone = phoneInput.filter { it.isDigit() }

            if (userExists) {
                // Редактирование существующего пользователя
                val updatedUser = user.copy(
                    name = nameInput,
                    email = emailInput,
                    phone = cleanPhone,
                    avatarUrl = avatarUrl
                )
                userDao.updateUser(updatedUser)

                user = updatedUser
            } else {
                // Новый пользователь
                val newUser = User(
                    avatarUrl = avatarUrl,
                    name = nameInput,
                    email = emailInput,
                    phone = cleanPhone,
                    isRegistered = true
                )
                userDao.insertUser(newUser)

                user = newUser
            }

            isEditing = false
            userExists = true
        }
    }
}