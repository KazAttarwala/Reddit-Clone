package edu.cs371m.reddit.api

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.core.text.clearSpans
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class RedditPost (
    @SerializedName("name")
    val key: String,
    @SerializedName("title")
    val title: SpannableString,
    @SerializedName("subreddit")
    val subreddit: String,
    @SerializedName("score")
    val score: Int,
    @SerializedName("author")
    val author: String,
    @SerializedName("num_comments")
    val commentCount: Int,
    @SerializedName("thumbnail")
    val thumbnailURL: String,
    @SerializedName("url")
    val imageURL: String,
    @SerializedName("selftext")
    val selfText : SpannableString?,
    @SerializedName("is_video")
    val isVideo : Boolean,
    // Useful for subreddits
    @SerializedName("display_name")
    val displayName: SpannableString?,
    @SerializedName("icon_img")
    val iconURL: String?,
    @SerializedName("public_description")
    val publicDescription: SpannableString?
): Serializable {
    companion object {
        private fun findAndSetSpan(fulltext: SpannableString, subtext: String): Boolean {
            if (subtext.isEmpty()) return true
            val i = fulltext.indexOf(subtext, ignoreCase = true)
            if (i == -1) return false
            fulltext.setSpan(
                ForegroundColorSpan(Color.CYAN), i, i + subtext.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return true
        }

        fun spannableStringsEqual(a: SpannableString?, b: SpannableString?): Boolean {
            if(a == null && b == null) return true
            if(a == null && b != null) return false
            if(a != null && b == null) return false
            val spA = a!!.getSpans(0, a.length, Any::class.java)
            val spB = b!!.getSpans(0, b.length, Any::class.java)
            return a.toString() == b.toString()
                    &&
                    spA.size == spB.size && spA.equals(spB)

        }
    }
    private fun clearSpan(str: SpannableString?) {
        str?.clearSpans()
        str?.setSpan(
            ForegroundColorSpan(Color.GRAY), 0, 0,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    
    private fun removeAllCurrentSpans(){
        clearSpan(title)
        clearSpan(selfText)
        clearSpan(displayName)
        clearSpan(publicDescription)
    }

    fun searchFor(searchTerm: String): Boolean {
        removeAllCurrentSpans()
        
        if (searchTerm.isEmpty()) return true
        
        if (displayName.isNullOrEmpty()) {
            val foundInTitle = findAndSetSpan(title, searchTerm)
            val foundInSelfText = if (selfText != null) findAndSetSpan(selfText, searchTerm) else false
            return foundInTitle || foundInSelfText
        } else {
            val foundInDisplayName = findAndSetSpan(displayName, searchTerm)
            val foundInDescription = if (publicDescription != null) findAndSetSpan(publicDescription, searchTerm) else false
            return foundInDisplayName || foundInDescription
        }
    }


    override fun equals(other: Any?) : Boolean =
        if (other is RedditPost) {
            key == other.key
        } else {
            false
        }

    override fun hashCode(): Int {
        var result = key.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + score
        result = 31 * result + author.hashCode()
        result = 31 * result + commentCount
        result = 31 * result + thumbnailURL.hashCode()
        result = 31 * result + imageURL.hashCode()
        result = 31 * result + (selfText?.hashCode() ?: 0)
        result = 31 * result + isVideo.hashCode()
        result = 31 * result + (displayName?.hashCode() ?: 0)
        result = 31 * result + (iconURL?.hashCode() ?: 0)
        result = 31 * result + (publicDescription?.hashCode() ?: 0)
        return result
    }
}
