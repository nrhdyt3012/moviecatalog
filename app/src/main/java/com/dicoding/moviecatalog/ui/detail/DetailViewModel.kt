package com.dicoding.moviecatalog.ui.detail

import androidx.lifecycle.ViewModel
import com.dicoding.moviecatalog.core.domain.model.Movie
import com.dicoding.moviecatalog.core.domain.usecase.MovieUseCase

class DetailMovieViewModel(private val movieUseCase: MovieUseCase) : ViewModel() {
    fun setFavoriteMovie(movie: Movie, newStatus: Boolean) =
        movieUseCase.setFavoriteMovie(movie, newStatus)
}