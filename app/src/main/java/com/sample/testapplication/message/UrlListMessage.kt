package com.sample.testapplication.message

import com.sample.testapplication.model.Url
import ph.adsaway.message.BaseMessage


class UrlListMessage(
    `object`: Any,
    val urlList: List<Url>,
    val changeSort: Boolean
) : BaseMessage(`object`)
