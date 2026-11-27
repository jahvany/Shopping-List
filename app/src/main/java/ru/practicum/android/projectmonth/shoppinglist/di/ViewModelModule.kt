package ru.practicum.android.projectmonth.shoppinglist.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.practicum.android.projectmonth.shoppinglist.presentation.viewmodel.AuthViewModel
import ru.practicum.android.projectmonth.shoppinglist.presentation.viewmodel.ProductsViewModel
import ru.practicum.android.projectmonth.shoppinglist.presentation.viewmodel.ShoppingListsViewModel

val viewModelModule = module {

    viewModel { ShoppingListsViewModel(get(), get(), get(), get()) }
    viewModel { ProductsViewModel(get(), get(), get(), get()) }
    viewModel { AuthViewModel(get()) }
}
