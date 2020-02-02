package com.sample.testapplication.viewmodel

import android.app.Application
import android.text.InputType
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.firestore.Query
import com.sample.testapplication.R
import com.sample.testapplication.data.repository.UrlRepository
import com.sample.testapplication.data.services.UrlService
import com.sample.testapplication.message.*
import com.sample.testapplication.model.DialogParams
import com.sample.testapplication.model.Url
import com.sample.testapplication.utils.CallableWithParams
import com.sample.testapplication.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus


class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext
    private val repository: UrlRepository = UrlRepository(UrlService())
    var isLoadingVisible: ObservableBoolean = ObservableBoolean(false)
    var sortDirection = Query.Direction.ASCENDING


    init {
        getUrlList(sortDirection, false)
    }

    fun getUrlList(sort: Query.Direction, isChangeSort: Boolean = true) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                EventBus.getDefault().post(DialogMessage(this, type = Constants.DialogType.SHOW_LOADING))

                withContext(Dispatchers.IO) {
                    sortDirection = sort
                    val urlList = repository.getUrls(sort)

                    withContext(Dispatchers.Main) {
                        if (urlList.isNotEmpty()) {
                            EventBus.getDefault().post(
                                UrlListMessage(
                                    this,
                                    urlList,
                                    isChangeSort
                                )
                            )
                        }

                        EventBus.getDefault().post(DialogMessage(this, type = Constants.DialogType.DISMISS_LOADING))
                    }
                }
            }
        }
    }

    fun loadMore(firstItem: Url?, lastItem: Url?) {
        viewModelScope.launch(Dispatchers.IO) {
            val urlList = repository.getUrls(sortDirection)

            withContext(Dispatchers.Main) {
                if (urlList.isNotEmpty()) {
                    EventBus.getDefault().post(
                        UrlListMessage(
                            this,
                            urlList,
                            false
                        )
                    )
                }
                isLoadingVisible.set(false)
            }
        }
    }

    fun onAddClick() {
        viewModelScope.launch(Dispatchers.Main) {
            val callback: CallableWithParams = object : CallableWithParams() {
                @Throws(Exception::class)
                override fun call(): Void? {
                    if (params != null){
                        val params = params as DialogParams
                        saveUrl(params.text, params.dialog)
                    }

                    return null
                }
            }

            EventBus.getDefault().post(InputDialogMessage(
                this,
                title = R.string.title_add_url,
                hintLabel = R.string.hint_input_url,
                inputType = InputType.TYPE_TEXT_VARIATION_URI,
                positiveCallback = callback)
            )
        }
    }

    fun deleteItems(ids: ArrayList<String>) {
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                EventBus.getDefault().post(DialogMessage(this, type = Constants.DialogType.SHOW_LOADING))
                withContext(Dispatchers.IO) {
                    val result = repository.deleteUrls(ids)
                    withContext(Dispatchers.Main) {
                        if (result) {
                            EventBus.getDefault().post(UrlRemoveMessage(this, ids))
                        }
                        EventBus.getDefault().post(DialogMessage(this, type = Constants.DialogType.DISMISS_LOADING))
                    }
                }
            }
        }
    }

    private fun saveUrl(url: String, dialog: MaterialDialog){
        viewModelScope.launch {
            withContext(Dispatchers.Main){
                EventBus.getDefault().post(DialogMessage(this, type = Constants.DialogType.SHOW_LOADING))
                withContext(Dispatchers.IO) {
                    val createdUrl = repository.saveUrl(url)
                    withContext(Dispatchers.Main) {
                        if (createdUrl != null) {
                            EventBus.getDefault().post(UrlMessage(
                                this,
                                createdUrl
                            ))
                            dialog.dismiss()
                        }
                        EventBus.getDefault().post(DialogMessage(this, type = Constants.DialogType.DISMISS_LOADING))
                    }
                }
            }
        }
    }
}