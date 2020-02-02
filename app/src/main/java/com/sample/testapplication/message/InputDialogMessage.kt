package com.sample.testapplication.message

import com.sample.testapplication.R
import com.sample.testapplication.utils.CallableWithParams
import ph.adsaway.message.BaseMessage


class InputDialogMessage(
    `object`: Any,
    val title: Any? = R.string.cm_input,
    val hintLabel: Int? = R.string.hint_common_input,
    val positiveLabel: Any? = R.string.cm_submit,
    val positiveCallback: CallableWithParams,
    val onSubmit: Boolean = true,
    val allowEmpty: Boolean = false,
    val inputType: Int? = null,
    val preFill: String? = null
) : BaseMessage(`object`)
