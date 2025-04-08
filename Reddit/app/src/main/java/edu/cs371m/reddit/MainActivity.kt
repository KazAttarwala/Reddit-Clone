package edu.cs371m.reddit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import edu.cs371m.reddit.api.RedditApi
import edu.cs371m.reddit.api.RedditPostRepository
import edu.cs371m.reddit.databinding.ActionBarBinding
import edu.cs371m.reddit.databinding.ActivityMainBinding
import edu.cs371m.reddit.ui.HomeFragmentDirections
import edu.cs371m.reddit.ui.MainViewModel
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    companion object {
        var globalDebug = false
        lateinit var jsonAww100: String
        lateinit var subreddit1: String
    }
    private var actionBarBinding: ActionBarBinding? = null
    private val viewModel: MainViewModel by viewModels()
    private lateinit var navController : NavController


    fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.rootView.windowToken, 0)
    }

    private fun initActionBar(actionBar: ActionBar) {
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBarBinding = ActionBarBinding.inflate(layoutInflater)
        actionBar.customView = actionBarBinding?.root
        viewModel.initActionBarBinding(actionBarBinding!!)
    }


    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.
        getAction(direction.actionId)?.
        run {
            navigate(direction)
        }
    }
    private fun actionBarTitleLaunchSubreddit() {
        actionBarBinding?.actionTitle?.setOnClickListener {
            Log.d("MainActivity", "Title clicked, navigating to subreddit")
            val direction = HomeFragmentDirections.actionHomeFragmentToSubreddits()
            navController.safeNavigate(direction)
            hideKeyboard()
        }
    }
    private fun actionBarLaunchFavorites() {
        actionBarBinding?.actionFavorite?.setOnClickListener {
            Log.d("MainActivity", "Favorites clicked, navigating to favorites")
            val direction = HomeFragmentDirections.actionHomeFragmentToFavorites()
            navController.safeNavigate(direction)
            hideKeyboard()
        }
    }

    private fun actionBarSearch() {
        actionBarBinding?.actionSearch?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val searchText = s.toString()
                viewModel.setSearchTerm(searchText)
                
                if (searchText.isEmpty()) {
                    hideKeyboard()
                }
                
                Log.d("MainActivity", "Search term changed to: $searchText")
            }
        })
    }

    private fun initDebug() {
        if(globalDebug) {
            assets.list("")?.forEach {
                Log.d(javaClass.simpleName, "Asset file: $it" )
            }
            jsonAww100 = assets.open("aww.hot.1.100.json.transformed.txt").bufferedReader().use {
                it.readText()
            }
            subreddit1 = assets.open("subreddits.1.json.txt").bufferedReader().use {
                it.readText()
            }
        }
    }
    private fun initTitleObservers() {
        viewModel.observeTitle().observe(this) { title ->
            Log.d("MainActivity", "Title changed to: $title")
            actionBarBinding?.actionTitle?.text = title
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDebug()
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        setSupportActionBar(activityMainBinding.toolbar)
        supportActionBar?.let{
            initActionBar(it)
        }

        initTitleObservers()
        actionBarTitleLaunchSubreddit()
        actionBarLaunchFavorites()
        actionBarSearch()

        navController = findNavController(R.id.main_frame)
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        activityMainBinding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}
