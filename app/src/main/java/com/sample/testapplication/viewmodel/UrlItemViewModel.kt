package com.sample.testapplication.viewmodel

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import com.sample.testapplication.model.Url


class UrlItemViewModel(application: Application) : AndroidViewModel(application) {
    var url: Url? = null
    var isSelected: ObservableBoolean = ObservableBoolean(false)
}