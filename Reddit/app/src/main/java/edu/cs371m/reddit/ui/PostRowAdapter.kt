package edu.cs371m.reddit.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.cs371m.reddit.R
import edu.cs371m.reddit.api.RedditPost
import edu.cs371m.reddit.databinding.RowPostBinding
import edu.cs371m.reddit.glide.Glide

/**
 * Created by witchel on 8/25/2019
 */

// https://developer.android.com/reference/androidx/recyclerview/widget/ListAdapter
// Slick adapter that provides submitList, so you don't worry about how to update
// the list, you just submit a new one when you want to change the list and the
// Diff class computes the smallest set of changes that need to happen.
// NB: Both the old and new lists must both be in memory at the same time.
// So you can copy the old list, change it into a new list, then submit the new list.
//
// You can call adapterPosition to get the index of the selected item
class PostRowAdapter(private val viewModel: MainViewModel,
                     private val navigateToOnePost: (RedditPost)->Unit )
    : ListAdapter<RedditPost, PostRowAdapter.VH>(RedditDiff()) {
    class RedditDiff : DiffUtil.ItemCallback<RedditPost>() {
        override fun areItemsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
            return oldItem.key == newItem.key
        }
        override fun areContentsTheSame(oldItem: RedditPost, newItem: RedditPost): Boolean {
            return RedditPost.spannableStringsEqual(oldItem.title, newItem.title) &&
                    RedditPost.spannableStringsEqual(oldItem.selfText, newItem.selfText) &&
                    RedditPost.spannableStringsEqual(oldItem.publicDescription, newItem.publicDescription) &&
                    RedditPost.spannableStringsEqual(oldItem.displayName, newItem.displayName)

        }
    }

    inner class VH(val binding: RowPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: RedditPost) {
            binding.title.text = post.title

            if (post.selfText.isNullOrEmpty()) {
                binding.selfText.visibility = View.GONE
            } else {
                binding.selfText.visibility = View.VISIBLE
                binding.selfText.text = post.selfText
            }

            binding.score.text = post.score.toString()
            binding.comments.text = post.commentCount.toString()

            Glide.glideFetch(post.imageURL, post.thumbnailURL, binding.image)

            viewModel.observeFavorites().observe(binding.root.context as androidx.lifecycle.LifecycleOwner) { favorites ->
                if (favorites.any { it.key == post.key }) {
                    binding.rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
                } else {
                    binding.rowFav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                }
            }

            binding.rowFav.setOnClickListener {
                val favorites = viewModel.observeFavorites().value ?: emptyList()
                if (favorites.any { it.key == post.key }) {
                    viewModel.removeFavorite(post)
                    binding.rowFav.setImageResource(R.drawable.ic_favorite_border_black_24dp)
                } else {
                    viewModel.addFavorite(post)
                    binding.rowFav.setImageResource(R.drawable.ic_favorite_black_24dp)
                }
            }

            val clickListener = View.OnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    navigateToOnePost(getItem(position))
                }
            }

            binding.title.setOnClickListener(clickListener)
            binding.selfText.setOnClickListener(clickListener)
            binding.image.setOnClickListener(clickListener)
            binding.root.setOnClickListener(clickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = RowPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val post = getItem(position)
        holder.bind(post)
    }
}

