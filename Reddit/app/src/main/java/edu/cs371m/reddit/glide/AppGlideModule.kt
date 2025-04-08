package edu.cs371m.reddit.glide

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.text.Html
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import edu.cs371m.reddit.MainActivity
import edu.cs371m.reddit.R


@GlideModule
class AppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        builder.setLogLevel(Log.ERROR)
    }
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}

object Glide {
    private val width = Resources.getSystem().displayMetrics.widthPixels
    private val height = Resources.getSystem().displayMetrics.heightPixels
    private var glideOptions = RequestOptions ()
        .fitCenter()
        .transform(RoundedCorners (20))

    private fun fromHtml(source: String): String {
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY).toString()
    }
    
    private fun assetFetch(urlString: String, imageView: ImageView) {
        GlideApp.with(imageView.context)
            .load(urlString)
            .apply(glideOptions)
            .override(width, height)
            .into(imageView)
        if (urlString.endsWith("bigcat0.jpg")) {
            imageView.tag = "bigcat0.jpg"
        } else if (urlString.endsWith("bigcat1.jpg")) {
            imageView.tag = "bigcat1.jpg"
        } else if (urlString.endsWith("bigcat2.jpg")) {
            imageView.tag = "bigcat2.jpg"
        } else if (urlString.endsWith("bigdog0.jpg")) {
            imageView.tag = "bigdog0.jpg"
        }
    }
    fun glideFetch(urlString: String, thumbnailURL: String, imageView: ImageView) {
        if (MainActivity.globalDebug) {
           assetFetch(urlString, imageView)
        } else {
            GlideApp.with(imageView.context)
                .asBitmap()
                .load(fromHtml(urlString))
                .apply(glideOptions)
                .error(R.color.colorAccent)
                .override(width, height)
                .error(
                    GlideApp.with(imageView.context)
                        .asBitmap()
                        .load(fromHtml(thumbnailURL))
                        .apply(glideOptions)
                        .error(R.color.colorAccent)
                        .override(500, 500)
                )
                .into(imageView)
        }
    }
}
