package com.example.auth

import android.content.Context
import com.example.data.AppDatabase
import com.example.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest

class AuthManager private constructor(context: Context) {
    private val userDao = AppDatabase.getDatabase(context).userDao()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun register(username: String, passwordRaw: String, role: String): Result<Unit> {
        val existing = userDao.getUserByUsername(username)
        if (existing != null) {
            return Result.failure(Exception("Username already exists"))
        }
        val user = UserEntity(
            username = username,
            passwordHash = hashPassword(passwordRaw),
            role = role
        )
        userDao.insertUser(user)
        // Automatically login after register
        val createdUser = userDao.getUserByUsername(username)
        _currentUser.value = createdUser
        return Result.success(Unit)
    }

    suspend fun login(username: String, passwordRaw: String): Result<Unit> {
        val user = userDao.getUserByUsername(username)
        if (user == null || user.passwordHash != hashPassword(passwordRaw)) {
            return Result.failure(Exception("Invalid username or password"))
        }
        _currentUser.value = user
        return Result.success(Unit)
    }

    fun logout() {
        _currentUser.value = null
    }

    companion object {
        @Volatile
        private var INSTANCE: AuthManager? = null

        fun getInstance(context: Context): AuthManager {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
