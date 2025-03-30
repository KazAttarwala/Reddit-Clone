package edu.cs371m.reddit.ui.subreddits

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.cs371m.reddit.api.RedditPost
import edu.cs371m.reddit.databinding.RowSubredditBinding
import edu.cs371m.reddit.glide.Glide
import edu.cs371m.reddit.ui.MainViewModel
import edu.cs371m.reddit.ui.PostRowAdapter

class SubredditListAdapter(private val viewModel: MainViewModel,
                           private val navController: NavController
)
    : ListAdapter<RedditPost, SubredditListAdapter.VH>(PostRowAdapter.RedditDiff()) {

    // ViewHolder pattern
    inner class VH(private val binding: RowSubredditBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: RedditPost) {
            // ...existing code (if any)...
            binding.subRowHeading.text = item.displayName
            binding.subRowDetails.text = item.publicDescription
            // Load image using Glide if available
            Glide.glideFetch(item.iconURL ?: "", item.iconURL ?: "", binding.subRowPic)

            // Set click listeners for heading and picture
            val clickListener = {
                // navigate to posts for this subreddit

            }
            binding.subRowHeading.setOnClickListener { clickListener() }
            binding.subRowPic.setOnClickListener { clickListener() }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RowSubredditBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }
}
