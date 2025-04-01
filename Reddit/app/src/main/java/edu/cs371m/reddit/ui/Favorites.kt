package edu.cs371m.reddit.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cs371m.reddit.R
import edu.cs371m.reddit.databinding.FragmentRvBinding

class Favorites: Fragment(R.layout.fragment_rv) {
    // XXX initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(javaClass.simpleName, "onViewCreated")
        val binding = FragmentRvBinding.bind(view)
        viewModel.setTitle("Favorites")
        viewModel.hideActionBarFavorites()
        
        // Disable swipe refresh in this fragment
        binding.swipeRefreshLayout.isEnabled = false
        
        // Set up RecyclerView with PostRowAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = PostRowAdapter(viewModel) { post ->
            // Navigate to OnePostFragment when a post is selected
            val action = FavoritesDirections.actionFavoritesToOnePostFragment(post)
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        // Observe filtered search favorites that will show all favorites when search is empty
        viewModel.observeFavorites().observe(viewLifecycleOwner) { filteredFavorites ->
            adapter.submitList(filteredFavorites)
        }
    }

    override fun onDestroyView() {
        // Restore action bar favorites icon when leaving this fragment
        viewModel.showActionBarFavorites()
        super.onDestroyView()
    }
}