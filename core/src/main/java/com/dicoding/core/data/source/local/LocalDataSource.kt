package com.dicoding.moviecatalog.core.data.source.local

import com.dicoding.moviecatalog.core.data.source.local.entity.MovieEntity
import com.dicoding.moviecatalog.core.data.source.local.room.MovieDao
import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val movieDao: MovieDao) {

    fun getAllMovies(): Flow<List<MovieEntity>> = movieDao.getAllMovies()

    fun getFavoriteMovies(): Flow<List<MovieEntity>> = movieDao.getFavoriteMovies()

    suspend fun insertMovies(movieList: List<MovieEntity>) = movieDao.insertMovies(movieList)

    fun setFavoriteMovie(movie: MovieEntity, newState: Boolean) {
        movie.isFavorite = newState
        movieDao.updateFavoriteMovie(movie)
    }
}