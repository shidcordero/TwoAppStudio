package com.sample.testapplication.model

import com.sample.testapplication.utils.FirebaseModel

data class Url(
    val link: String? = null,
    val name: String? = null,
    val imageLink: String? = null
) : FirebaseModel()