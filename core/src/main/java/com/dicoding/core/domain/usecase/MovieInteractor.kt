package com.dicoding.moviecatalog.core.domain.usecase

import com.dicoding.moviecatalog.core.data.Resource
import com.dicoding.moviecatalog.core.domain.model.Movie
import com.dicoding.moviecatalog.core.domain.repository.IMovieRepository
import kotlinx.coroutines.flow.Flow

class MovieInteractor(private val movieRepository: IMovieRepository) : MovieUseCase {

    override fun getAllMovies(): Flow<Resource<List<Movie>>> =
        movieRepository.getAllMovies()

    override fun searchMovies(query: String): Flow<Resource<List<Movie>>> =
        movieRepository.searchMovies(query)

    override fun getFavoriteMovies(): Flow<List<Movie>> =
        movieRepository.getFavoriteMovies()

    override fun setFavoriteMovie(movie: Movie, state: Boolean) =
        movieRepository.setFavoriteMovie(movie, state)
}