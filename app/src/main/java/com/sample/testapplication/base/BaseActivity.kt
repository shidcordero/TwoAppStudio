package com.sample.testapplication.base

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sample.testapplication.message.DialogMessage
import com.sample.testapplication.message.InputDialogMessage
import com.sample.testapplication.message.IntentMessage
import com.sample.testapplication.utils.Constants
import com.sample.testapplication.utils.DialogUtils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onIntentEvent(message: IntentMessage<Any>){
        val intent = Intent(this, message.intentClass)
        if (message.isClearTop!!){
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onDialogEvent(message: DialogMessage){
        when (message.type) {
            Constants.DialogType.COMMON -> {
                DialogUtils.showMessage(
                    this@BaseActivity,
                    message.title!!,
                    message.message!!,
                    message.positiveLabel!!,
                    message.positiveCallback,
                    message.negativeLabel,
                    message.negativeCallback
                )
            }
            Constants.DialogType.SHOW_LOADING -> {
                DialogUtils.showLoadingDialog(this@BaseActivity)
            }
            Constants.DialogType.DISMISS_LOADING -> {
                DialogUtils.dismissLoadingDialog()
            }
            Constants.DialogType.INTERNET -> {
                DialogUtils.showInternetError(this@BaseActivity)
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInputDialogEvent(message: InputDialogMessage){
        DialogUtils.showInputDialog(
            context = this@BaseActivity,
            title = message.title,
            positiveLabel = message.positiveLabel,
            hintLabel = message.hintLabel,
            positiveCallback = message.positiveCallback,
            onSubmit = message.onSubmit,
            allowEmpty = message.allowEmpty,
            inputType = message.inputType!!,
            preFill = message.preFill
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this)
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

}