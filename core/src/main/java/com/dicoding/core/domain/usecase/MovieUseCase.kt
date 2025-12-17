package com.dicoding.moviecatalog.core.domain.usecase

import android.content.res.Resources
import com.dicoding.moviecatalog.core.data.Resource
import com.dicoding.moviecatalog.core.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieUseCase {
    fun getAllMovies(): Flow<Resource<List<Movie>>>
    fun searchMovies(query: String): Flow<Resource<List<Movie>>>
    fun getFavoriteMovies(): Flow<List<Movie>>
    fun setFavoriteMovie(movie: Movie, state: Boolean)
}