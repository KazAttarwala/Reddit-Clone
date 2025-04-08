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

class MainViewModel : ViewModel() {
    private var title = MutableLiveData<String>()
    private var searchTerm = MutableLiveData<String>()
    private var subreddit = MutableLiveData<String>().apply {
        value = "aww"
    }
    private var actionBarBinding : ActionBarBinding? = null
    private val redditApi = RedditApi.create()
    private val redditPostRepository = RedditPostRepository(redditApi)
    private var favorites = MutableLiveData<List<RedditPost>>().apply {
        value = emptyList()
    }
    private var netSubreddits = MutableLiveData<List<RedditPost>>().apply{
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
    private var netPosts = MediatorLiveData<List<RedditPost>>().apply {
        addSource(subreddit) { subreddit: String ->
            Log.d("repoPosts", subreddit)
            viewModelScope.launch (
                context = viewModelScope.coroutineContext + Dispatchers.IO)
            {
                try {
                    val posts = redditPostRepository.getPosts(subreddit)
                    withContext(Dispatchers.Main) {
                        value = posts
                    }
                    Log.d("netPosts", "Fetched ${posts.size} posts for subreddit $subreddit")
                } catch (e: Exception) {
                    Log.e("netPosts", "Error fetching posts for subreddit $subreddit", e)
                    withContext(Dispatchers.Main) {
                        value = emptyList()
                    }
                }
            }
        }
    }

    private var searchPosts = MediatorLiveData<List<RedditPost>>().apply {
        addSource(netPosts) { posts ->
            val currentSearchTerm = searchTerm.value ?: ""
            value = posts.filter { it.searchFor(currentSearchTerm) }
        }
        
        addSource(searchTerm) { term ->
            val posts = netPosts.value ?: emptyList()
            value = posts.filter { it.searchFor(term ?: "") }
        }
    }
    
    private var searchSubreddits = MediatorLiveData<List<RedditPost>>().apply {
        addSource(netSubreddits) { subreddits ->
            val currentSearchTerm = searchTerm.value ?: ""
            value = subreddits.filter { it.searchFor(currentSearchTerm) }
        }
        
        addSource(searchTerm) { term ->
            val subreddits = netSubreddits.value ?: emptyList()
            value = subreddits.filter { it.searchFor(term ?: "") }
        }
    }
    
    private var searchFavorites = MediatorLiveData<List<RedditPost>>().apply {
        addSource(favorites) { favs ->
            val currentSearchTerm = searchTerm.value ?: ""
            value = favs.filter { it.searchFor(currentSearchTerm) }
        }
        
        addSource(searchTerm) { term ->
            val favs = favorites.value ?: emptyList()
            value = favs.filter { it.searchFor(term ?: "") }
        }
    }

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
        return searchSubreddits
    }
    fun observePosts(): LiveData<List<RedditPost>> {
        return searchPosts
    }

    fun setSearchTerm(term: String) {
        searchTerm.value = term
    }

    fun setSubreddit(name: String) {
        subreddit.value = name
    }

    fun observeSearchPost(post: RedditPost): LiveData<RedditPost> {
        val searchPost = MediatorLiveData<RedditPost>().apply {
            addSource(netPosts) { posts ->
                val foundPost = posts.find { it.key == post.key }
                if (foundPost != null) {
                    value = foundPost
                }
            }
            
            addSource(favorites) { favPosts ->
                val foundPost = favPosts.find { it.key == post.key }
                if (foundPost != null) {
                    value = foundPost
                }
            }
            
            addSource(searchTerm) { term ->
                val currentValue = value ?: post
                currentValue.searchFor(term ?: "")
                value = currentValue
            }
        }
        return searchPost
    }

    /////////////////////////
    // Action bar
    fun initActionBarBinding(it: ActionBarBinding) {
        actionBarBinding = it
    }
    fun hideActionBarFavorites() {
        actionBarBinding?.actionFavorite?.visibility = View.GONE
    }
    fun showActionBarFavorites() {
        actionBarBinding?.actionFavorite?.visibility = View.VISIBLE
    }

    fun observeFavorites(): LiveData<List<RedditPost>> {
        return searchFavorites
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
