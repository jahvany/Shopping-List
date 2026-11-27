package ru.practicum.android.projectmonth.shoppinglist.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.practicum.android.projectmonth.shoppinglist.domain.ThemeRepository
import ru.practicum.android.projectmonth.shoppinglist.domain.models.ShoppingList
import ru.practicum.android.projectmonth.shoppinglist.domain.usecaces.AuthInteractor
import ru.practicum.android.projectmonth.shoppinglist.domain.usecaces.ProductInteractor
import ru.practicum.android.projectmonth.shoppinglist.domain.usecaces.ShoppingListInteractor
import ru.practicum.android.projectmonth.shoppinglist.presentation.state.ShoppingListsState

private const val DEFAULT_SHOPPING_LIST_ICON = "ic_shopping_list_default"

class ShoppingListsViewModel(
    private val shoppingListInteractor: ShoppingListInteractor,
    private val productInteractor: ProductInteractor,
    private val authInteractor: AuthInteractor,
    private val themeRepository: ThemeRepository
) : ViewModel() {

    var uiState by mutableStateOf<ShoppingListsState>(ShoppingListsState.Empty)
        private set

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    var darkModeEnabled by mutableStateOf<Boolean>(false)
        private set

    init {
        getShoppingLists()

        viewModelScope.launch {
            darkModeEnabled = themeRepository.darkModeEnabled.first()
        }
    }

    fun getShoppingLists() {
        viewModelScope.launch {
            val result = shoppingListInteractor.getAllShoppingLists()

            uiState = if (result.isNotEmpty()) {
                ShoppingListsState.Content(result)
            } else {
                ShoppingListsState.Empty
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setIsSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    fun newShoppingList(name: String) {
        viewModelScope.launch {
            shoppingListInteractor.saveNewShoppingList(
                ShoppingList(
                    id = 0L,
                    name = name,
                    iconRes = DEFAULT_SHOPPING_LIST_ICON,
                    products = emptyList(),
                    sortType = 0,
                    login = authInteractor.currentUser()
                )
            ).collect { _ ->
                getShoppingLists()
            }
        }
    }

    fun deleteShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            shoppingListInteractor.deleteShoppingList(shoppingList)
            getShoppingLists()
        }
    }

    fun deleteAllShoppingLists() {
        viewModelScope.launch {
            shoppingListInteractor.deleteAllShoppingLists()
            getShoppingLists()
        }
    }

    fun updateShoppingList(shoppingList: ShoppingList, newName: String) {
        if (newName.isBlank() || newName == shoppingList.name) return
        viewModelScope.launch {
            val updatedList = shoppingList.copy(name = newName)
            shoppingListInteractor.updateShoppingList(updatedList.id, updatedList).collect { _ ->
                getShoppingLists()
            }
        }
    }

    fun updateShoppingListIcon(shoppingList: ShoppingList, newIconKey: String) {
        if (newIconKey == shoppingList.iconRes) return
        viewModelScope.launch {
            val updatedList = shoppingList.copy(iconRes = newIconKey)
            shoppingListInteractor.updateShoppingList(updatedList.id, updatedList).collect { _ ->
                getShoppingLists()
            }
        }
    }

    fun copyShoppingList(shoppingList: ShoppingList) {
        viewModelScope.launch {
            val newName = "${shoppingList.name} (копия)"
            var newShoppingListId = 0L

            shoppingListInteractor.saveNewShoppingList(
                ShoppingList(
                    id = 0L,
                    name = newName,
                    iconRes = shoppingList.iconRes,
                    products = emptyList(),
                    login = authInteractor.currentUser()
                )
            ).collect { newList ->
                newShoppingListId = newList.id
            }

            val products = productInteractor.getProductsByShoppingListId(shoppingList.id).first()

            products.forEach { product ->
                productInteractor.saveNewProduct(
                    product.copy(
                        id = 0L,
                        name = product.name,
                        checked = false,
                        measureUnit = product.measureUnit,
                        shoppingListId = newShoppingListId
                    )
                ).collect { _ -> }
            }

            getShoppingLists()
        }
    }

    fun switchTheme() {
        viewModelScope.launch {
            themeRepository.switchTheme(!darkModeEnabled)
        }
    }
}
