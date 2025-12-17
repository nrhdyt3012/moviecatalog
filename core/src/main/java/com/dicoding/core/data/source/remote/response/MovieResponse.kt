package com.dicoding.moviecatalog.core.data.source.remote.response

import com.google.gson.annotations.SerializedName

data class MovieListResponse(
    @SerializedName("results")
    val results: List<MovieResponse>
)

data class MovieResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("title")
    val title: String,

    @SerializedName("overview")
    val overview: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("release_date")
    val releaseDate: String
)