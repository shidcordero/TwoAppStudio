package com.sample.testapplication.utils

import androidx.databinding.BaseObservable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties


@IgnoreExtraProperties
open class FirebaseModel : BaseObservable() {
    @set:Exclude @get:Exclude
    var id: String? = null

    fun <T : FirebaseModel?> withId(id: String): T {
        this.id = id
        return this as T
    }
}