package ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import ru.practicum.android.projectmonth.shoppinglist.R
import ru.practicum.android.projectmonth.shoppinglist.core.navigation.Destination
import ru.practicum.android.projectmonth.shoppinglist.domain.models.ShoppingList
import ru.practicum.android.projectmonth.shoppinglist.presentation.state.ShoppingListsState
import ru.practicum.android.projectmonth.shoppinglist.presentation.viewmodel.ShoppingListsViewModel
import ru.practicum.android.projectmonth.shoppinglist.ui.components.CustomFab
import ru.practicum.android.projectmonth.shoppinglist.ui.components.IllustratedMessage
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components.DeleteAllListsDialog
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components.DeleteListDialog
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components.IconSelectionBottomSheet
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components.NewShoppingListDialog
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components.RenameShoppingListDialog
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components.ShoppingListsTopBar
import ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists.components.SwipeableShoppingListItem

@Composable
fun ShoppingListsScreen(
    navController: NavController,
    viewModel: ShoppingListsViewModel
) {
    val uiState = viewModel.uiState
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()

    val darkModeEnabled by viewModel.darkModeEnabled.collectAsStateWithLifecycle(
        initialValue = false
    )

    var showAddingDialog by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    var showDeleteListDialog by remember { mutableStateOf<ShoppingList?>(null) }
    var showRenameDialog by remember { mutableStateOf<ShoppingList?>(null) }
    var showIconSelector by remember { mutableStateOf(false) }
    var selectedListForIcon by remember { mutableStateOf<ShoppingList?>(null) }

    val allLists = (uiState as? ShoppingListsState.Content)?.data ?: emptyList()
    val filteredLists = if (isSearchActive && searchQuery.isNotEmpty()) {
        allLists.filter { it.name.startsWith(searchQuery, ignoreCase = true) }
    } else {
        allLists
    }

    LaunchedEffect(Unit) {
        viewModel.getShoppingLists()
    }

    BackHandler(enabled = isSearchActive) {
        viewModel.setIsSearchActive(false)
    }

    Scaffold(
        topBar = {
            ShoppingListsTopBar(
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                darkModeEnabled = darkModeEnabled,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onSearchClick = { viewModel.setIsSearchActive(true) },
                onSearchClose = { viewModel.setIsSearchActive(false) },
                onDeleteAllClick = {
                    if (allLists.isNotEmpty()) {
                        showDeleteAllDialog = true
                    }
                },
                onSwitchThemeClick = {
                    viewModel.switchTheme()
                }
            )
        },
        floatingActionButton = {
            if (!isSearchActive) {
                CustomFab(
                    onClick = { showAddingDialog = true }
                )
            }
        }
    ) { innerPadding ->

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState is ShoppingListsState.Empty && !isSearchActive -> {
                    IllustratedMessage(
                        imageResId = R.drawable.img_shopping_lists,
                        headerResId = R.string.shopping_lists_screen_header,
                        messageResId = R.string.shopping_lists_screen_message,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                isSearchActive && searchQuery.isNotEmpty() && filteredLists.isEmpty() -> {
                    IllustratedMessage(
                        imageResId = R.drawable.img_search_not_found,
                        headerResId = R.string.search_not_found_header,
                        messageResId = R.string.search_not_found_message,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
                filteredLists.isNotEmpty() -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = innerPadding
                    ) {
                        items(
                            count = filteredLists.size,
                            key = { index -> filteredLists[index].id }
                        ) { index ->
                            SwipeableShoppingListItem(
                                item = filteredLists[index],
                                onItemClick = { shoppingList ->
                                    navController.navigate(Destination.Products.createRoute(shoppingList.id))
                                },
                                onDelete = { shoppingList ->
                                    showDeleteListDialog = shoppingList
                                },
                                onRename = { shoppingList ->
                                    showRenameDialog = shoppingList
                                },
                                onCopy = { shoppingList ->
                                    viewModel.copyShoppingList(shoppingList)
                                },
                                onIconLongClick = { shoppingList ->
                                    selectedListForIcon = shoppingList
                                    showIconSelector = true
                                }
                            )
                        }
                    }
                }
                else -> {
                    Box(modifier = Modifier.padding(innerPadding))
                }
            }

            if (isSearchActive && searchQuery.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                        .clickable {
                            viewModel.setIsSearchActive(false)
                        }
                )
            }
        }

        if (showAddingDialog) {
            NewShoppingListDialog(
                onDismissRequest = { showAddingDialog = false },
                onConfirm = { newShoppingListName ->
                    if (newShoppingListName.isNotBlank()) {
                        viewModel.newShoppingList(name = newShoppingListName)
                    }
                    showAddingDialog = false
                }
            )
        }

        if (showDeleteAllDialog) {
            DeleteAllListsDialog(
                onDismiss = { showDeleteAllDialog = false },
                onConfirm = {
                    viewModel.deleteAllShoppingLists()
                    showDeleteAllDialog = false
                }
            )
        }

        showDeleteListDialog?.let { shoppingList ->
            DeleteListDialog(
                listName = shoppingList.name,
                onDismiss = { showDeleteListDialog = null },
                onConfirm = {
                    viewModel.deleteShoppingList(shoppingList)
                    showDeleteListDialog = null
                }
            )
        }

        showRenameDialog?.let { shoppingList ->
            RenameShoppingListDialog(
                shoppingList = shoppingList,
                onDismiss = { showRenameDialog = null },
                onConfirm = { newName ->
                    if (newName.isNotBlank() && newName != shoppingList.name) {
                        viewModel.updateShoppingList(shoppingList, newName)
                    }
                    showRenameDialog = null
                }
            )
        }

        if (showIconSelector && selectedListForIcon != null) {
            IconSelectionBottomSheet(
                currentIconDbKey = selectedListForIcon!!.iconRes,
                onIconSelected = { appIcon ->
                    selectedListForIcon?.let { list ->
                        viewModel.updateShoppingListIcon(list, appIcon.dbKey)
                    }
                    showIconSelector = false
                    selectedListForIcon = null
                },
                onDismiss = {
                    showIconSelector = false
                    selectedListForIcon = null
                }
            )
        }
    }
}
