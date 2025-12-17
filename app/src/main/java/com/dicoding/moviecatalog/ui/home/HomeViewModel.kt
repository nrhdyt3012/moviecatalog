package com.dicoding.moviecatalog.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dicoding.moviecatalog.core.domain.usecase.MovieUseCase

class HomeViewModel(movieUseCase: MovieUseCase) : ViewModel() {
    val movies = movieUseCase.getAllMovies().asLiveData()

    fun searchMovies(query: String) = movieUseCase.searchMovies(query).asLiveData()
}