package com.example.floramarket.data

import androidx.room.*
import com.example.floramarket.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Query("SELECT * FROM users WHERE isRegistered = 1 LIMIT 1")
    fun getActiveUser(): Flow<User?>

    @Query("SELECT * FROM users WHERE isRegistered = 1 LIMIT 1")
    suspend fun getActiveUserOnce(): User?

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE email = :email AND isRegistered = 1 LIMIT 1")
    suspend fun getActiveUserByEmail(email: String): User?

    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("UPDATE users SET isRegistered = 0")
    suspend fun logoutAllUsers()

    @Query("SELECT COUNT(*) FROM users WHERE isRegistered = 1")
    suspend fun getActiveUserCount(): Int
}