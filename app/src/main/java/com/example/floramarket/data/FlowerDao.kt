package com.example.floramarket.data

import androidx.room.*
import com.example.floramarket.model.Flower
import kotlinx.coroutines.flow.Flow

@Dao
interface FlowerDao {

    @Query("SELECT * FROM flowers")
    fun getAllFlowers(): Flow<List<Flower>>

    @Query("SELECT * FROM flowers")
    suspend fun getAllFlowersOnce(): List<Flower>

    @Query("SELECT * FROM flowers WHERE isFavorite = 1")
    fun getFavoriteFlowers(): Flow<List<Flower>>

    @Query("SELECT * FROM flowers WHERE cartQuantity > 0")
    fun getCartFlowers(): Flow<List<Flower>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlower(flower: Flower)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlowers(flowers: List<Flower>)

    @Update
    suspend fun updateFlower(flower: Flower)

    @Query("UPDATE flowers SET isFavorite = :isFavorite WHERE id = :flowerId")
    suspend fun updateFavorite(flowerId: String, isFavorite: Boolean)

    @Query("UPDATE flowers SET cartQuantity = :quantity WHERE id = :flowerId")
    suspend fun updateCartQuantity(flowerId: String, quantity: Int)

    @Query("DELETE FROM flowers")
    suspend fun deleteAll()
}