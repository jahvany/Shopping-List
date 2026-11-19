package ru.practicum.android.projectmonth.shoppinglist.ui.screens.shopping_lists

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
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
import ru.practicum.android.projectmonth.shoppinglist.ui.theme.LightBackground
import ru.practicum.android.projectmonth.shoppinglist.ui.theme.LightBrownElements
import ru.practicum.android.projectmonth.shoppinglist.ui.theme.MediumDarkText

@Composable
fun ShoppingListsScreen(
    navController: NavController,
    viewModel: ShoppingListsViewModel
) {
    val uiState = viewModel.uiState
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()

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
                navController = navController,
                isSearchActive = isSearchActive,
                searchQuery = searchQuery,
                onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                onSearchClick = { viewModel.setIsSearchActive(true) },
                onSearchClose = { viewModel.setIsSearchActive(false) },
                onDeleteAllClick = {
                    if (allLists.isNotEmpty()) {
                        showDeleteAllDialog = true
                    }
                },
                onSwitchThemeClick = {

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

@Composable
fun ShoppingListsItem(
    item: ShoppingList,
    onClick: (ShoppingList) -> Unit,
    onIconLongClick: (ShoppingList) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val resources = LocalResources.current

    val iconResId = remember(resources, item.iconRes) {
        resources.getIdentifier(
            item.iconRes,
            "drawable",
            context.packageName
        ).takeIf { it != 0 } ?: R.drawable.ic_shopping_list_default
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .border(
                width = 1.dp,
                color = Color(0xFFD0D0D0),
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = LightBackground,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp)
            .clickable {
                onClick(item)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    color = LightBrownElements,
                    shape = CircleShape
                )
                .combinedClickable(
                    onClick = { },
                    onLongClick = { onIconLongClick(item) }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
                tint = MediumDarkText,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = item.name,
            style = MaterialTheme.typography.labelLarge,
            color = MediumDarkText
        )
    }
}
