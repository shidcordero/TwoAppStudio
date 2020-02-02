package com.sample.testapplication

import androidx.multidex.MultiDexApplication
import com.sample.testapplication.viewmodel.MainViewModel
import com.sample.testapplication.viewmodel.UrlItemViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        setup()
    }

    private fun setup() {
        if (GlobalContext.getOrNull() == null) {
            val viewModelModule = module {
                viewModel { MainViewModel(this@App) }
                viewModel { UrlItemViewModel(this@App) }
            }

            startKoin {
                androidLogger()
                androidContext(this@App)
                modules(viewModelModule)
            }
        }
    }
}