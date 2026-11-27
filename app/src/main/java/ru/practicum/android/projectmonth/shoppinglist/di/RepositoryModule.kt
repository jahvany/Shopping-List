package ru.practicum.android.projectmonth.shoppinglist.di

import org.koin.dsl.module
import ru.practicum.android.projectmonth.shoppinglist.data.AuthRepositoryImpl
import ru.practicum.android.projectmonth.shoppinglist.data.ProductRepositoryImpl
import ru.practicum.android.projectmonth.shoppinglist.data.ShoppingListRepositoryImpl
import ru.practicum.android.projectmonth.shoppinglist.data.ThemeRepositoryImpl
import ru.practicum.android.projectmonth.shoppinglist.domain.AuthRepository
import ru.practicum.android.projectmonth.shoppinglist.domain.ProductRepository
import ru.practicum.android.projectmonth.shoppinglist.domain.ShoppingListRepository
import ru.practicum.android.projectmonth.shoppinglist.domain.ThemeRepository

val repositoryModule = module {

    single<ProductRepository> {
        ProductRepositoryImpl(get(), get())
    }

    single<ShoppingListRepository> {
        ShoppingListRepositoryImpl(get(), get())
    }

    single<AuthRepository> {
        AuthRepositoryImpl(get(), get(), get())
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }

}

