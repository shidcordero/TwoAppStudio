package com.sample.testapplication.utils

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.sample.testapplication.R
import com.sample.testapplication.message.DialogMessage
import org.greenrobot.eventbus.EventBus
import java.net.MalformedURLException


object ApiHelper {
    fun <T> handleErrorResponse(ex: T) {
        if (ex is MalformedURLException){
            EventBus.getDefault().post(DialogMessage(
                `object` = this,
                title = R.string.cm_invalid,
                message = R.string.em_invalid_url
            ))
        } else {
            EventBus.getDefault().post(
                DialogMessage(
                    `object` = this,
                    title = R.string.cm_common_error,
                    message = R.string.cm_common_error_desc
                )
            )
        }
    }
}





