package com.dicoding.moviecatalog.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.dicoding.moviecatalog.R
import com.dicoding.moviecatalog.core.domain.model.Movie
import com.dicoding.moviecatalog.databinding.FragmentDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailMovieFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val detailViewModel: DetailMovieViewModel by viewModel()
    private val args: DetailMovieFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movie = args.movie
        showDetailMovie(movie)
    }

    private fun showDetailMovie(movie: Movie) {
        with(binding) {
            tvTitle.text = movie.title
            tvRating.text = String.format("%.1f", movie.voteAverage)
            tvReleaseDate.text = getString(R.string.release_date_format, movie.releaseDate)
            tvOverview.text = movie.overview

            Glide.with(requireContext())
                .load("https://image.tmdb.org/t/p/w500${movie.posterPath}")
                .placeholder(R.drawable.ic_movie_placeholder)
                .into(ivPoster)

            Glide.with(requireContext())
                .load("https://image.tmdb.org/t/p/w500${movie.backdropPath}")
                .placeholder(R.drawable.ic_movie_placeholder)
                .into(ivBackdrop)

            var statusFavorite = movie.isFavorite
            setFavoriteState(statusFavorite)

            fabFavorite.setOnClickListener {
                statusFavorite = !statusFavorite
                detailViewModel.setFavoriteMovie(movie, statusFavorite)
                setFavoriteState(statusFavorite)
            }
        }
    }

    private fun setFavoriteState(isFavorite: Boolean) {
        binding.fabFavorite.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_border
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}