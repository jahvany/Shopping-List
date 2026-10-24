package ru.practicum.android.projectmonth.shoppinglist.domain

import ru.practicum.android.projectmonth.shoppinglist.domain.models.ShoppingList

interface ShoppingListRepository {

    suspend fun getAllShoppingLists(login: String): List<ShoppingList>
    suspend fun getShoppingListById(id: Long): ShoppingList?
    suspend fun updateShoppingList(id: Long, shoppingList: ShoppingList, login: String): ShoppingList
    suspend fun saveNewShoppingList(shoppingList: ShoppingList, login: String): ShoppingList
    suspend fun deleteShoppingList(shoppingList: ShoppingList)
    suspend fun deleteAllShoppingLists(login: String)
}
