package com.dicoding.moviecatalog.core.utils

import com.dicoding.moviecatalog.core.data.source.local.entity.MovieEntity
import com.dicoding.moviecatalog.core.data.source.remote.response.MovieResponse
import com.dicoding.moviecatalog.core.domain.model.Movie

object DataMapper {
    fun mapResponsesToEntities(input: List<MovieResponse>): List<MovieEntity> {
        val movieList = ArrayList<MovieEntity>()
        input.map {
            val movie = MovieEntity(
                id = it.id,
                title = it.title,
                overview = it.overview,
                posterPath = it.posterPath,
                backdropPath = it.backdropPath,
                voteAverage = it.voteAverage,
                releaseDate = it.releaseDate,
                isFavorite = false
            )
            movieList.add(movie)
        }
        return movieList
    }

    fun mapEntitiesToDomain(input: List<MovieEntity>): List<Movie> =
        input.map {
            Movie(
                id = it.id,
                title = it.title,
                overview = it.overview,
                posterPath = it.posterPath,
                backdropPath = it.backdropPath,
                voteAverage = it.voteAverage,
                releaseDate = it.releaseDate,
                isFavorite = it.isFavorite
            )
        }

    fun mapDomainToEntity(input: Movie) = MovieEntity(
        id = input.id,
        title = input.title,
        overview = input.overview,
        posterPath = input.posterPath,
        backdropPath = input.backdropPath,
        voteAverage = input.voteAverage,
        releaseDate = input.releaseDate,
        isFavorite = input.isFavorite
    )
}