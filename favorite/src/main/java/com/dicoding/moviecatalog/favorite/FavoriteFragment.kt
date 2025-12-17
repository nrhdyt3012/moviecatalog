package com.dicoding.moviecatalog.favorite

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.dicoding.moviecatalog.favorite.databinding.FragmentFavoriteBinding
import com.dicoding.moviecatalog.ui.home.MovieAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules

class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private val favoriteViewModel: FavoriteViewModel by viewModel()
    private lateinit var movieAdapter: MovieAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        loadKoinModules(favoriteModule)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeFavoriteMovies()
    }

    private fun setupRecyclerView() {
        movieAdapter = MovieAdapter()
        movieAdapter.onItemClick = { selectedData ->
            val action = FavoriteFragmentDirections.actionFavoriteToDetail(selectedData)
            findNavController().navigate(action)
        }

        binding.rvFavoriteMovies.apply {
            layoutManager = GridLayoutManager(context, 2)
            setHasFixedSize(true)
            adapter = movieAdapter
        }
    }

    private fun observeFavoriteMovies() {
        favoriteViewModel.favoriteMovies.observe(viewLifecycleOwner) { movies ->
            if (movies.isNullOrEmpty()) {
                showEmpty(true)
            } else {
                showEmpty(false)
                movieAdapter.setData(movies)
            }
        }
    }

    private fun showEmpty(isEmpty: Boolean) {
        binding.tvEmptyFavorite.isVisible = isEmpty
        binding.rvFavoriteMovies.isVisible = !isEmpty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvFavoriteMovies.adapter = null
        _binding = null
    }
}