package ru.practicum.android.projectmonth.shoppinglist.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ru.practicum.android.projectmonth.shoppinglist.data.db.entity.ShoppingListEntity
import ru.practicum.android.projectmonth.shoppinglist.data.db.relations.ShoppingListWithProducts

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shopping_list where login = :login")
    suspend fun getAll(login: String): List<ShoppingListEntity>

    @Query("SELECT * FROM shopping_list WHERE id = :id")
    suspend fun getById(id: Long): ShoppingListEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shoppingList: ShoppingListEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg shoppingLists: ShoppingListEntity): List<Long>

    @Update
    suspend fun update(shoppingList: ShoppingListEntity): Int

    @Query("DELETE FROM shopping_list WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("DELETE FROM shopping_list WHERE login = :login")
    suspend fun deleteAll(login: String): Int

    @Delete
    suspend fun delete(shoppingList: ShoppingListEntity): Int

    @Transaction
    @Query("SELECT * FROM shopping_list")
    fun getAllWithProducts(): List<ShoppingListWithProducts>

    @Transaction
    @Query("SELECT * FROM shopping_list WHERE id = :id")
    fun getByIdWithProducts(id: Long): ShoppingListWithProducts?
}
