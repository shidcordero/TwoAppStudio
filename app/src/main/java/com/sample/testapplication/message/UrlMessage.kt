package com.sample.testapplication.message

import com.sample.testapplication.model.Url
import ph.adsaway.message.BaseMessage


class UrlMessage(
    `object`: Any,
    val url: Url
) : BaseMessage(`object`)
