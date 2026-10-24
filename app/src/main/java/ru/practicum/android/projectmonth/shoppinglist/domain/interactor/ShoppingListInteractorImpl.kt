package ru.practicum.android.projectmonth.shoppinglist.domain.interactor

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.practicum.android.projectmonth.shoppinglist.domain.AuthRepository
import ru.practicum.android.projectmonth.shoppinglist.domain.ShoppingListRepository
import ru.practicum.android.projectmonth.shoppinglist.domain.models.ShoppingList
import ru.practicum.android.projectmonth.shoppinglist.domain.usecaces.ShoppingListInteractor

class ShoppingListInteractorImpl(
    val repository: ShoppingListRepository,
    val authRepository: AuthRepository
) : ShoppingListInteractor {

    override suspend fun getAllShoppingLists(): List<ShoppingList> {
        Log.d("ShoppingListInteractor", "getAllShoppingLists START, login=${authRepository.currentUser()}")
        val result = repository.getAllShoppingLists(authRepository.currentUser())
        Log.d("ShoppingListInteractor", "getAllShoppingLists RESULT: ${result.size}")
        return result
    }

    override fun getShoppingListById(id: Long): Flow<ShoppingList?> = flow {
        emit(repository.getShoppingListById(id))
    }
        .flowOn(Dispatchers.IO)

    override fun updateShoppingList(
        id: Long,
        shoppingList: ShoppingList
    ): Flow<ShoppingList> = flow {
        emit(repository.updateShoppingList(id, shoppingList, authRepository.currentUser()))
    }
        .flowOn(Dispatchers.IO)


    override fun saveNewShoppingList(shoppingList: ShoppingList): Flow<ShoppingList> = flow {
        emit(repository.saveNewShoppingList(shoppingList, authRepository.currentUser()))
    }
        .flowOn(Dispatchers.IO)

    override suspend fun deleteShoppingList(shoppingList: ShoppingList) {
        repository.deleteShoppingList(shoppingList)
    }

    override suspend fun deleteAllShoppingLists() {
        repository.deleteAllShoppingLists(login = authRepository.currentUser())
    }
}
