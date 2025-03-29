package edu.cs371m.reddit.ui


import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cs371m.reddit.api.RedditApi
import edu.cs371m.reddit.api.RedditPost
import edu.cs371m.reddit.api.RedditPostRepository
import edu.cs371m.reddit.databinding.ActionBarBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// XXX Much to write
class MainViewModel : ViewModel() {
    private var title = MutableLiveData<String>()
    private var searchTerm = MutableLiveData<String>()
    private var subreddit = MutableLiveData<String>().apply {
        value = "aww"
    }
    private var actionBarBinding : ActionBarBinding? = null
    // XXX Write me, api, repository, favorites
    private val redditApi = RedditApi.create()
    private val redditPostRepository = RedditPostRepository(redditApi)
    // netSubreddits fetches the list of subreddits
    // We only do this once, so technically it does not need to be
    // MutableLiveData, or even really LiveData.  But maybe in the future
    // we will refetch it.
    private var netSubreddits = MutableLiveData<List<RedditPost>>().apply{
        // XXX Write me, viewModelScope.launch getSubreddits()
        viewModelScope.launch (
            context = viewModelScope.coroutineContext + Dispatchers.IO)
        {
            try {
                val subreddits = redditPostRepository.getSubreddits()
                withContext(Dispatchers.Main) {
                    value = subreddits
                }
                Log.d("netSubreddits", "Fetched ${subreddits.size} subreddits")
            } catch (e: Exception) {
                Log.e("netSubreddits", "Error fetching subreddits", e)
                withContext(Dispatchers.Main) {
                    value = emptyList()
                }
            }
        }
    }
    // netPosts fetches the posts for the current subreddit, when that
    // changes
    private var netPosts = MediatorLiveData<List<RedditPost>>().apply {
        addSource(subreddit) { subreddit: String ->
            Log.d("repoPosts", subreddit)
            // XXX Write me, viewModelScope.launch getPosts
            viewModelScope.launch (
                context = viewModelScope.coroutineContext + Dispatchers.IO)
            {
                try {
                    val posts = redditPostRepository.getPosts(subreddit)
                    // Switch back to the main thread to update LiveData
                    withContext(Dispatchers.Main) {
                        value = posts
                    }
                    Log.d("netPosts", "Fetched ${posts.size} posts for subreddit $subreddit")
                } catch (e: Exception) {
                    Log.e("netPosts", "Error fetching posts for subreddit $subreddit", e)
                    // If there is an error, we set the value to an empty list
                    withContext(Dispatchers.Main) {
                        value = emptyList()
                    }
                }
            }
        }
    }
    // XXX Write me MediatorLiveData searchSubreddit, searchFavorites
    // searchPosts

    // Looks pointless, but if LiveData is set up properly, it will fetch posts
    // from the network
    fun repoFetch() {
        val fetch = subreddit.value
        subreddit.value = fetch
    }

    fun observeTitle(): LiveData<String> {
        return title
    }
    fun setTitle(newTitle: String) {
        title.value = newTitle
    }
    
    fun observeSubreddit(): LiveData<String> {
        return subreddit
    }
    fun observeSubreddits(): LiveData<List<RedditPost>> {
        return netSubreddits
    }
    fun observePosts(): LiveData<List<RedditPost>> {
        return netPosts
    }


    // ONLY call this from OnePostFragment, otherwise you will have problems.
    fun observeSearchPost(post: RedditPost): LiveData<RedditPost> {
        val searchPost = MediatorLiveData<RedditPost>().apply {
            // XXX Write me
        }
        return searchPost
    }

    /////////////////////////
    // Action bar
    fun initActionBarBinding(it: ActionBarBinding) {
        // XXX Write me, one liner
    }
    fun hideActionBarFavorites() {
        // XXX Write me, one liner
    }
    fun showActionBarFavorites() {
        // XXX Write me, one liner
    }

    // XXX Write me, set, observe, deal with favorites
    private var favorites = MutableLiveData<List<RedditPost>>().apply {
        value = emptyList()
    }
    fun observeFavorites(): LiveData<List<RedditPost>> {
        return favorites
    }
    fun addFavorite(post: RedditPost) {
        val currentFavorites = favorites.value ?: emptyList()
        if (!currentFavorites.contains(post)) {
            favorites.value = currentFavorites + post
            Log.d("addFavorite", "Added ${post.title} to favorites")
        }
    }
    fun removeFavorite(post: RedditPost) {
        val currentFavorites = favorites.value ?: emptyList()
        if (currentFavorites.contains(post)) {
            favorites.value = currentFavorites - post
            Log.d("removeFavorite", "Removed ${post.title} from favorites")
        }
    }
}