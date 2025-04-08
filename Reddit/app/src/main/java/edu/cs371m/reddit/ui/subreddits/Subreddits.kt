package edu.cs371m.reddit.ui.subreddits

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cs371m.reddit.R
import edu.cs371m.reddit.databinding.FragmentRvBinding
import edu.cs371m.reddit.ui.MainViewModel

class Subreddits : Fragment(R.layout.fragment_rv) {
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRvBinding.bind(view)
        Log.d(javaClass.simpleName, "Subreddits onViewCreated")

        binding.swipeRefreshLayout.isEnabled = false
        
        viewModel.setTitle("Subreddits")
        viewModel.hideActionBarFavorites()
        
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = SubredditListAdapter(viewModel, findNavController())
        binding.recyclerView.adapter = adapter

        viewModel.observeSubreddits().observe(viewLifecycleOwner) { subreddits ->
            adapter.submitList(subreddits)
        }
    }

    override fun onDestroyView() {
        viewModel.showActionBarFavorites()
        super.onDestroyView()
    }
}
