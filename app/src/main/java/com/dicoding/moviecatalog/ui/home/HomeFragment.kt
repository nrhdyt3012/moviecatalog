package com.dicoding.moviecatalog.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.moviecatalog.R
import com.dicoding.moviecatalog.core.data.Resource
import com.dicoding.moviecatalog.core.domain.model.Movie
import com.dicoding.moviecatalog.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var movieAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        observeMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter()
        movieAdapter.onItemClick = { selectedData ->
            val action = HomeFragmentDirections.actionHomeToDetail(selectedData)
            findNavController().navigate(action)
        }

        binding.rvMovies.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = movieAdapter
        }
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isNotEmpty()) {
                    searchMovies(query)
                } else {
                    observeMovies()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeMovies() {
        homeViewModel.movies.observe(viewLifecycleOwner) { movies ->
            handleResource(movies)
        }
    }

    private fun searchMovies(query: String) {
        homeViewModel.searchMovies(query).observe(viewLifecycleOwner) { movies ->
            handleResource(movies)
        }
    }

    private fun handleResource(resource: Resource<List<Movie>>?) {
        when (resource) {
            is Resource.Loading -> showLoading(true)
            is Resource.Success -> {
                showLoading(false)
                resource.data?.let { data ->
                    if (data.isEmpty()) {
                        showEmpty(true)
                    } else {
                        showEmpty(false)
                        movieAdapter.setData(data)
                    }
                }
            }
            is Resource.Error -> {
                showLoading(false)
                showError(true, resource.message ?: getString(R.string.error_message))
            }
            else -> {}
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.rvMovies.isVisible = !isLoading
        binding.tvEmpty.isVisible = false
        binding.tvError.isVisible = false
    }

    private fun showEmpty(isEmpty: Boolean) {
        binding.tvEmpty.isVisible = isEmpty
        binding.rvMovies.isVisible = !isEmpty
        binding.progressBar.isVisible = false
        binding.tvError.isVisible = false
    }

    private fun showError(isError: Boolean, message: String = "") {
        binding.tvError.isVisible = isError
        binding.tvError.text = message
        binding.rvMovies.isVisible = !isError
        binding.progressBar.isVisible = false
        binding.tvEmpty.isVisible = false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvMovies.adapter = null
        _binding = null
    }
}