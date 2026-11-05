package ru.practicum.android.projectmonth.shoppinglist.ui.screens.products

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.domain.models.Product
import ru.practicum.android.projectmonth.shoppinglist.domain.models.SortType
import ru.practicum.android.projectmonth.shoppinglist.presentation.state.ProductsState
import ru.practicum.android.projectmonth.shoppinglist.presentation.viewmodel.ProductsViewModel
import ru.practicum.android.projectmonth.shoppinglist.ui.components.CustomFab
import ru.practicum.android.projectmonth.shoppinglist.ui.components.IllustratedMessage
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components.NewProductBottomSheet
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components.ProductsMenu
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components.ProductsTopBar
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.products.components.products_list.ProductsList
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components.DeleteConfirmationDialog
import ru.practicum.android.projectmonth.shoppinglist.ui.theme.BottomSheetPeach

// Используется только здесь, нет необходимости выносить в тему
val productListDividerColor = Color(0xFFCAC4D0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    navController: NavController,
    viewModel: ProductsViewModel
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState(
            initialValue = SheetValue.Hidden,
            skipHiddenState = false
        )
    )

    val uiState = viewModel.uiState
    val currentSortType = viewModel.currentSortType
    val productsSuggestions = viewModel.productSuggestions

    // Переменные для получения значений из диалога добавления/редактирования товара
    var newProductName by remember { mutableStateOf("") }
    var newProductNumber by remember { mutableFloatStateOf(0f) }
    var newProductUnit by remember { mutableStateOf("") }

    // Флаги отображения меню и диалогов
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showDeletePurchasedDialog by remember { mutableStateOf(false) }

    // Товары для удаления/редактирования
    var productToChange by remember { mutableStateOf<Product?>(null) }
    var productToDelete by remember { mutableStateOf<Product?>(null) }

    // Флаг видимости диалога добавления/редактирования
    val isBottomSheetVisible = scaffoldState.bottomSheetState.targetValue == SheetValue.Expanded

    // Затемнение фона при открытии диалога
    val backgroundAlfa by remember(isBottomSheetVisible) {
        derivedStateOf {
            if (isBottomSheetVisible) {
                scaffoldState.bottomSheetState.requireOffset().coerceIn(0f, 1f)
            } else 0f
        }
    }

    // Высота контейнера для расчета смещения кнопки добавления/подтверждения
    var containerHeightPx by remember { mutableIntStateOf(0) }

    // Обработка кликов по фону при открытом диалоге
    val onDarkBackgroundTap = {
        scope.launch { scaffoldState.bottomSheetState.hide() }
    }

    Box(
        Modifier
            .fillMaxSize()
            .onSizeChanged { containerHeightPx = it.height }
    ) {
        BottomSheetScaffold(
            topBar = {
                // Верхняя панель
                ProductsTopBar(
                    navController = navController,
                    onMenuClick = {
                        showMenu = true
                    }
                )

                // Затемнение верхней панели
                if (backgroundAlfa > 0f) {
                    DarkBackgroundBox(
                        backgroundAlfa = backgroundAlfa,
                        onTap = onDarkBackgroundTap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                    )
                }
            },
            sheetContent = {
                // Диалог добавления/редактирования
                NewProductBottomSheet(
                    onNameChange = { name ->
                        newProductName = name
                    },
                    onNumberChange = { number ->
                        newProductNumber = number
                    },
                    onUnitChange = { unit ->
                        newProductUnit = unit
                    },
                    productToChange = productToChange,
                    suggestionsList = productsSuggestions
                )
            },
            scaffoldState = scaffoldState,
            sheetContainerColor = BottomSheetPeach,
            sheetPeekHeight = 0.dp
        ) { innerPadding ->

            // Основной контент
            when (uiState) {
                is ProductsState.Empty -> {
                    IllustratedMessage(
                        imageResId = R.drawable.img_products,
                        headerResId = R.string.products_screen_header,
                        messageResId = R.string.products_screen_message,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                is ProductsState.Content -> {
                    ProductsList(
                        products = uiState.data,
                        onCheckedChange = { product, isChecked ->
                            viewModel.checkProduct(product, isChecked)
                        },
                        onProductChange = { product ->
                            productToChange = product

                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                        onProductDelete = { product ->
                            productToDelete = product
                        },
                        isReorderable = (currentSortType == SortType.CUSTOM),
                        onReorder = { from, to ->
                            viewModel.moveProduct(from, to)
                        }
                    )
                }
            }

            // Затемнение фона
            if (backgroundAlfa > 0f) {
                DarkBackgroundBox(
                    backgroundAlfa = backgroundAlfa,
                    onTap = onDarkBackgroundTap,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Кнопка вызова диалога добавления товара + подтверждение добавления/изменения
        CustomFab(
            onClick = {
                scope.launch {
                    if (isBottomSheetVisible) {
                        scaffoldState.bottomSheetState.hide()

                        val currentProduct = productToChange

                        if (currentProduct == null) {
                            viewModel.addProduct(
                                name = newProductName,
                                number = newProductNumber,
                                measureUnit = newProductUnit
                            )
                        } else {
                            viewModel.updateProduct(
                                product = currentProduct,
                                name = newProductName,
                                number = newProductNumber,
                                measureUnit = newProductUnit
                            )
                            productToChange = null
                        }
                    } else {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            },
            iconResId = if (isBottomSheetVisible) R.drawable.ic_confirm else R.drawable.ic_add,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .offset {
                    val sheetTop = try {
                        scaffoldState.bottomSheetState.requireOffset()
                    } catch (_: IllegalStateException) {
                        containerHeightPx.toFloat()
                    }
                    IntOffset(0, (sheetTop - containerHeightPx).coerceAtMost(0f).toInt())
                }
        )

        // Вызов диалога удаления товара
        productToDelete?.let { product ->
            DeleteConfirmationDialog(
                title = stringResource(R.string.products_remove_dialog, product.name),
                onDismiss = {
                    productToDelete = null
                },
                onConfirm = {
                    viewModel.removeProduct(product.id)
                    productToDelete = null
                }
            )
        }

        // Вызов меню
        if (showMenu) {
            ProductsMenu(
                onDismissRequest = { showMenu = false },
                currentSortType = currentSortType,
                onSortTypeSelected = { selectedSort ->
                    viewModel.setSortType(selectedSort)
                },
                onDeleteAllClick = {
                    showMenu = false
                    showDeleteAllDialog = true
                },
                onClearPurchasedClick = {
                    showMenu = false
                    showDeletePurchasedDialog = true
                }
            )
        }

        // Вызов диалога удаления всех товаров
        if (showDeleteAllDialog) {
            DeleteConfirmationDialog(
                title = stringResource(R.string.products_delete_all_dialog),
                onDismiss = {
                    showDeleteAllDialog = false
                },
                onConfirm = {
                    viewModel.deleteAllProducts()
                    showDeleteAllDialog = false
                }
            )
        }

        // Вызов диалога удаления купленных товаров
        if (showDeletePurchasedDialog) {
            DeleteConfirmationDialog(
                title = stringResource(R.string.products_delete_purchased_dialog),
                onDismiss = {
                    showDeletePurchasedDialog = false
                },
                onConfirm = {
                    viewModel.clearPurchasedProducts()
                    showDeletePurchasedDialog = false
                }
            )
        }
    }
}

@Composable
fun DarkBackgroundBox(
    backgroundAlfa: Float,
    onTap: () -> Job,
    modifier: Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = backgroundAlfa * 0.6f))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onTap() })
            }
    )
}

