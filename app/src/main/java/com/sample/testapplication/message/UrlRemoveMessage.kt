package com.sample.testapplication.message

import ph.adsaway.message.BaseMessage


class UrlRemoveMessage(
    `object`: Any,
    val ids: ArrayList<String>
) : BaseMessage(`object`)
