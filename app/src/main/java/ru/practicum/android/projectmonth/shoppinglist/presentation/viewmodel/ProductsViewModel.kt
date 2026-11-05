package ru.practicum.android.projectmonth.shoppinglist.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.practicum.android.projectmonth.shoppinglist.domain.models.Product
import ru.practicum.android.projectmonth.shoppinglist.domain.usecaces.AuthInteractor
import ru.practicum.android.projectmonth.shoppinglist.domain.models.ShoppingList
import ru.practicum.android.projectmonth.shoppinglist.domain.models.SortType
import ru.practicum.android.projectmonth.shoppinglist.domain.usecaces.ProductInteractor
import ru.practicum.android.projectmonth.shoppinglist.domain.usecaces.ShoppingListInteractor
import ru.practicum.android.projectmonth.shoppinglist.presentation.state.ProductsState
import kotlin.collections.sortedBy

class ProductsViewModel(
    savedStateHandle: SavedStateHandle,
    private val productInteractor: ProductInteractor,
    private val shoppingListInteractor: ShoppingListInteractor,
    private val authInteractor: AuthInteractor
) : ViewModel() {

    val shoppingListId: Long = checkNotNull(savedStateHandle["shoppingListId"])

    var uiState by mutableStateOf<ProductsState>(ProductsState.Empty)
        private set

    var productSuggestions by mutableStateOf<List<String>>(emptyList())
        private set

    var currentSortType by mutableStateOf(SortType.NONE)
        private set

    private var shoppingList: ShoppingList? = null

    private var productsJob: Job? = null

    init {
        getProducts()
    }

    fun getProducts() {
        productsJob?.cancel()

        productsJob = viewModelScope.launch {
            // Получение подсказок в отдельной корутине
            launch {
                productInteractor
                    .getProductSuggests()
                    .collect { result ->
                        productSuggestions = result
                    }
            }

            val list = shoppingListInteractor.getShoppingListById(shoppingListId).first()
            shoppingList = list
            currentSortType = SortType.entries.find { it.index == list?.sortType } ?: SortType.NONE

            productInteractor.getProductsByShoppingListId(shoppingListId).collect { products ->
                val sortedProducts = applySort(products)

                uiState = if (sortedProducts.isNotEmpty()) {
                    ProductsState.Content(data = sortedProducts)
                } else {
                    ProductsState.Empty
                }
            }
        }
    }

    fun addProduct(name: String, number: Float, measureUnit: String) {
        viewModelScope.launch {
            productInteractor.saveNewProduct(
                Product(
                    id = 0L,
                    name = name.trim(),
                    checked = false,
                    number = number,
                    measureUnit = measureUnit,
                    shoppingListId = shoppingListId,
                    login = authInteractor.currentUser(),
                    sortPosition = when (val currentState = uiState) {
                        is ProductsState.Empty -> 0
                        is ProductsState.Content -> currentState.data.size
                    }
                )
            ).collect { }

            productInteractor.addProductSuggest(name.trim())
        }
    }

    fun checkProduct(product: Product, isChecked: Boolean) {
        viewModelScope.launch {
            productInteractor.updateProduct(
                id = product.id,
                product = product.copy(checked = isChecked)
            ).collect { }
        }
    }

    fun updateProduct(product: Product, name: String, number: Float, measureUnit: String) {
        viewModelScope.launch {
            productInteractor.updateProduct(
                id = product.id,
                product = product.copy(
                    name = name,
                    number = number,
                    measureUnit = measureUnit
                )
            ).collect { }
        }
    }

    fun removeProduct(productId: Long) {
        viewModelScope.launch {
            productInteractor.removeProduct(productId)
        }
    }

    fun clearPurchasedProducts() {
        val currentState = uiState

        if (currentState is ProductsState.Content) {
            val itemsToDelete = currentState.data.filter{ it.checked }

            if (itemsToDelete.isNotEmpty()) {
                viewModelScope.launch {
                    itemsToDelete.forEach { product ->
                        productInteractor.removeProduct(product.id)
                    }
                }
            }
        }
    }

    fun deleteAllProducts() {
        viewModelScope.launch {
            productInteractor.deleteShoppingListProducts(shoppingListId)
        }
    }

    fun setSortType(sortType: SortType) {
        currentSortType = sortType

        val currentState = uiState

        if (currentState is ProductsState.Content) {
            val products = currentState.data

            uiState = currentState.copy(data = applySort(products))
        }

        viewModelScope.launch {
            updateShoppingListSortType(sortType)
            if (sortType == SortType.ALPHABETICAL
                && currentSortType == sortType) { // На случай, если сортировка снова была изменена

                val latestState = uiState
                if (latestState is ProductsState.Content) {
                    updateProductsPositions(latestState.data)
                }
            }
        }
    }

    fun moveProduct(from: Int, to: Int) {
        val currentState = uiState

        if (currentState is ProductsState.Content) {
            val products = currentState.data.toMutableList().apply {
                add(to, removeAt(from))
            }

            viewModelScope.launch {
                updateProductsPositions(products)
            }

            uiState = currentState.copy(data = products)
        }
    }

    private suspend fun updateProductsPositions(products: List<Product>) {
        products.forEachIndexed { index, product ->
            if (product.sortPosition != index) {
                productInteractor.updateProduct(
                    id = product.id,
                    product = product.copy(
                        sortPosition = index
                    )
                ).collect {  }
            }
        }
    }

    private suspend fun updateShoppingListSortType(sortType: SortType) {
        shoppingList?.let { list ->
            shoppingListInteractor.updateShoppingList(
                id = shoppingListId,
                shoppingList = list.copy(sortType = sortType.index)
            ).collect { updatedList ->
                shoppingList = updatedList
            }
        }
    }

    private fun applySort(products: List<Product>): List<Product> {
        return when (currentSortType) {
            SortType.ALPHABETICAL -> products.sortedBy { it.name.lowercase() }
            SortType.CUSTOM -> products.sortedBy { it.sortPosition }
            SortType.NONE -> products
        }
    }
}
