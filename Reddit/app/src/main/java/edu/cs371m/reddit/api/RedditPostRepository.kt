package edu.cs371m.reddit.api

import android.text.SpannableString
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import edu.cs371m.reddit.MainActivity

class RedditPostRepository(private val redditApi: RedditApi) {
    private val gson : Gson = GsonBuilder().registerTypeAdapter(
            SpannableString::class.java, RedditApi.SpannableDeserializer()
        ).create()

    private fun unpackPosts(response: RedditApi.ListingResponse): List<RedditPost> {
        return response.data.children.map { it.data }
    }

    suspend fun getPosts(subreddit: String): List<RedditPost> {
        val response : RedditApi.ListingResponse?
        if (MainActivity.globalDebug) {
            response = gson.fromJson(
                MainActivity.jsonAww100,
                RedditApi.ListingResponse::class.java)
        } else {
            response = redditApi.getPosts(
                subreddit = subreddit,
                after = null,
                limit = 100
            ).execute().body()
        }
        return unpackPosts(response!!)
    }

    suspend fun getSubreddits(): List<RedditPost> {
        val response : RedditApi.ListingResponse?
        if (MainActivity.globalDebug) {
            response = gson.fromJson(
                MainActivity.subreddit1,
                RedditApi.ListingResponse::class.java)
        } else {
            response = redditApi.getSubreddits(
                after = null,
                limit = 100
            ).execute().body()
        }
        return unpackPosts(response!!)
    }
}
