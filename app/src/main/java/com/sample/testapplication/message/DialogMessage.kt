package com.sample.testapplication.message

import com.sample.testapplication.R
import com.sample.testapplication.utils.Constants
import ph.adsaway.message.BaseMessage
import java.util.concurrent.Callable


class DialogMessage(
    `object`: Any,
    val type: Constants.DialogType = Constants.DialogType.COMMON,
    val title: Any? = R.string.cm_common_error,
    val message: Any? = R.string.cm_common_error_desc,
    val positiveLabel: Any? = R.string.lm_ok,
    val positiveCallback: Callable<Any>? = null,
    val negativeLabel: Any? = null,
    val negativeCallback: Callable<Any>? = null
) : BaseMessage(`object`)
