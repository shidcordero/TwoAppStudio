package com.sample.testapplication.data.services

import com.google.firebase.firestore.*
import com.sample.testapplication.model.Url
import com.sample.testapplication.utils.asDeferredAsync
import kotlinx.coroutines.Deferred


class UrlService {

    private val firebaseDb: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    suspend fun getUrlsAsync(
        sortDirection: Query.Direction
    ): Deferred<QuerySnapshot> {
        return firebaseDb
            .collection("urls")
            .orderBy("name", sortDirection)
            .get()
            .asDeferredAsync()
    }

    suspend fun saveUrlAsync(url: Url): String {
        val id = firebaseDb.collection("urls").document().id
        saveUrlAsync(id, url).await()
        return id
    }

    fun deleteUrlsAsync(ids: ArrayList<String>): Deferred<Void>{
        val writeBatch: WriteBatch = firebaseDb.batch()

        for (i in 0 until ids.size) {
            val documentReference: DocumentReference =
                firebaseDb.collection("urls")
                    .document(ids[i])
            writeBatch.delete(documentReference)
        }

        return writeBatch.commit().asDeferredAsync()
    }

    private fun saveUrlAsync(id: String, url: Url): Deferred<Void> {
        return firebaseDb.collection("urls")
            .document(id)
            .set(url)
            .asDeferredAsync()
    }

}
