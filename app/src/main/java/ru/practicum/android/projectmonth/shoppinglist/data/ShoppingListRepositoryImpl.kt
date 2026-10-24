package ru.practicum.android.projectmonth.shoppinglist.data

import ru.practicum.android.projectmonth.shoppinglist.data.converter.db.ShoppingListDbConverter
import ru.practicum.android.projectmonth.shoppinglist.data.db.AppDatabase
import ru.practicum.android.projectmonth.shoppinglist.domain.ShoppingListRepository
import ru.practicum.android.projectmonth.shoppinglist.domain.models.ShoppingList

class ShoppingListRepositoryImpl(
    val appDatabase: AppDatabase,
    val shoppingListDbConverter: ShoppingListDbConverter,
): ShoppingListRepository {

    override suspend fun getAllShoppingLists(login: String): List<ShoppingList> {
        return appDatabase.shoppingListDao().getAll(login).map { shoppingListDbConverter.map(it) }
    }

    override suspend fun getShoppingListById(id: Long): ShoppingList {
        return shoppingListDbConverter.map(appDatabase.shoppingListDao().getById(id))
    }

    override suspend fun updateShoppingList(
        id: Long,
        shoppingList: ShoppingList,
        login: String
    ): ShoppingList {
        appDatabase.shoppingListDao().update(shoppingListDbConverter.map(shoppingList, login))
        return shoppingListDbConverter.map(appDatabase.shoppingListDao().getById(shoppingList.id))
    }

    override suspend fun saveNewShoppingList(shoppingList: ShoppingList, login: String): ShoppingList {
        val id = appDatabase.shoppingListDao().insert(shoppingListDbConverter.map(shoppingList, login))
        return shoppingListDbConverter.map(appDatabase.shoppingListDao().getById(id))
    }

    override suspend fun deleteShoppingList(shoppingList: ShoppingList) {
        appDatabase.shoppingListDao().deleteById(shoppingList.id)
    }

    override suspend fun deleteAllShoppingLists(login: String) {
        appDatabase.shoppingListDao().deleteAll(login)
    }
}
