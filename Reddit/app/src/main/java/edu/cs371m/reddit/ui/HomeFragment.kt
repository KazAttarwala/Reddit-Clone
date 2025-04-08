package edu.cs371m.reddit.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import edu.cs371m.reddit.R
import edu.cs371m.reddit.databinding.FragmentRvBinding

class HomeFragment: Fragment(R.layout.fragment_rv) {
    private val viewModel: MainViewModel by activityViewModels()

    private fun initAdapter(binding: FragmentRvBinding) {
        val postRowAdapter = PostRowAdapter(viewModel) {
            Log.d("OnePost",
                String.format("OnePost title %s",
                    if (it.title.length > 32)
                        it.title.substring(0, 31) + "..."
                    else it.title))
            Log.d("doOnePost", "image ${it.imageURL}")
            val action = HomeFragmentDirections
                .actionHomeFragmentToOnePostFragment(it)
            findNavController().navigate(action)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = postRowAdapter

        viewModel.observePosts().observe(viewLifecycleOwner) { posts ->
            Log.d("HomeFragment", "observeSearchPosts $posts")
            postRowAdapter.submitList(posts)
        }
    }

    private fun initSwipeLayout(swipe : SwipeRefreshLayout) {
        swipe.setOnRefreshListener {
            try {
                swipe.isRefreshing = true
                viewModel.repoFetch()
            } catch (e: Exception) {
                Log.e("HomeFragment", "Error fetching data: ${e.message}")
                Toast.makeText(context, "Error fetching data", Toast.LENGTH_SHORT).show()
            }
            swipe.isRefreshing = false
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRvBinding.bind(view)
        Log.d(javaClass.simpleName, "onViewCreated")
        initAdapter(binding)
        initSwipeLayout(binding.swipeRefreshLayout)

        viewModel.observeSubreddit().observe(viewLifecycleOwner) { subreddit ->
            viewModel.setTitle("r/${subreddit}")
        }
    }
}
