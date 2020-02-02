package com.sample.testapplication.data.repository

import com.google.firebase.firestore.Query
import com.sample.testapplication.data.services.UrlService
import com.sample.testapplication.model.Url
import com.sample.testapplication.utils.AndroidUtils
import com.sample.testapplication.utils.ApiHelper
import com.sample.testapplication.utils.FirebaseModel
import org.jsoup.Jsoup
import org.jsoup.nodes.Element


class UrlRepository(private val urlService: UrlService) {

    suspend fun getUrls(
        sortDirection: Query.Direction
    ): List<Url> {
        var result: Any? = null

        try {
            val data = urlService.getUrlsAsync(sortDirection).await()
            if (!data.isEmpty){
                result = data.documents.map {
                    it.toObject(Url::class.java)?.withId<FirebaseModel>(it.id)
                }
            }
        } catch (ex: Exception){
            ApiHelper.handleErrorResponse(ex)
        }

        return result as List<Url>
    }


    suspend fun saveUrl(url: String): Url? {
        var data: Url? = null

        try {
            val validUrl = AndroidUtils.getValidUrl(url)
            val document = Jsoup.connect(validUrl).get()
            val name = document.title()
            var imageLink: Any? = document.head().select("meta[itemprop=image]").firstOrNull()
            if (imageLink == null) {
                imageLink = document.head().select("link[href~=.*\\.(ico|png)]").firstOrNull()
                if (imageLink != null)
                    imageLink = imageLink.attr("href")
            } else {
                imageLink = validUrl + (imageLink as Element).attr("content")
            }

            data = Url(
                link = validUrl,
                name = name,
                imageLink = imageLink as String?
            )

            val id = urlService.saveUrlAsync(data)
            data.id = id
        } catch (ex: Exception){
            ApiHelper.handleErrorResponse(ex)
        }

        return data
    }

    suspend fun deleteUrls(ids: ArrayList<String>): Boolean {
        val result: Boolean

        result = try {
            urlService.deleteUrlsAsync(ids).await()
            true
        } catch (ex: Exception){
            ApiHelper.handleErrorResponse(ex)
            false
        }

        return result
    }
}