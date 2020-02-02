package com.sample.testapplication.message

import ph.adsaway.message.BaseMessage


class IntentMessage<T>(
    `object`: Any,
    val intentClass: Class<T>,
    val isClearTop: Boolean? = false
) : BaseMessage(`object`)
