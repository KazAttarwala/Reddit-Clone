package edu.cs371m.reddit.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import edu.cs371m.reddit.R
import edu.cs371m.reddit.api.RedditPost
import edu.cs371m.reddit.databinding.FragmentOnePostBinding
import edu.cs371m.reddit.glide.Glide

class OnePostFragment: Fragment(R.layout.fragment_one_post) {
    private val viewModel: MainViewModel by activityViewModels()
    private val args: OnePostFragmentArgs by navArgs()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentOnePostBinding.bind(view)
        Log.d(javaClass.simpleName, "onViewCreated for post: ${args.post}")
        
        // Hide action bar favorites icon in single post view
        viewModel.hideActionBarFavorites()
        // Set the title in action bar
        viewModel.setTitle("One Post")

        // Get the initial post
        displayPost(binding, args.post)
        
        // Observe search filtered post for highlighting
        viewModel.observeSearchPost(args.post).observe(viewLifecycleOwner) { post ->
            displayPost(binding, post)
        }
    }
    
    private fun displayPost(binding: FragmentOnePostBinding, post: RedditPost) {
        // Set post details
        binding.onePostSubreddit.text = "r/${post.subreddit}"
        binding.onePostTitle.text = post.title
        
        // Handle self text - show only if available
        if (post.selfText.isNullOrEmpty()) {
            binding.onePostSelfText.visibility = View.GONE
        } else {
            binding.onePostSelfText.visibility = View.VISIBLE
            binding.onePostSelfText.text = post.selfText
        }
        
        Glide.glideFetch(post.imageURL, post.thumbnailURL, binding.onePostImage)
    }
    
    override fun onDestroyView() {
        // Restore action bar favorites icon when leaving this fragment
        viewModel.showActionBarFavorites()
        super.onDestroyView()
    }
}
