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
    // initialize viewModel
    private val viewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRvBinding.bind(view)
        Log.d(javaClass.simpleName, "Subreddits onViewCreated")

        // Set title to "Subreddits"
        viewModel.setTitle("Subreddits")
        viewModel.hideActionBarFavorites()
        
        // Set up RecyclerView with SubredditListAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = SubredditListAdapter(viewModel, findNavController())
        binding.recyclerView.adapter = adapter

        // Observe filtered search subreddits that will show all subreddits when search is empty
        viewModel.observeSubreddits().observe(viewLifecycleOwner) { subreddits ->
            adapter.submitList(subreddits)
        }
    }

    override fun onDestroyView() {
        // Restore action bar favorites icon when leaving this fragment
        viewModel.showActionBarFavorites()
        super.onDestroyView()
    }
}
