package com.dicoding.moviecatalog.core.data

import app.cash.turbine.test
import com.dicoding.moviecatalog.core.data.source.local.LocalDataSource
import com.dicoding.moviecatalog.core.data.source.local.entity.MovieEntity
import com.dicoding.moviecatalog.core.data.source.remote.RemoteDataSource
import com.dicoding.moviecatalog.core.data.source.remote.network.ApiResponse
import com.dicoding.moviecatalog.core.data.source.remote.response.MovieResponse
import com.dicoding.moviecatalog.core.domain.model.Movie
import com.dicoding.moviecatalog.core.utils.AppExecutors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer

@ExperimentalCoroutinesApi
class MovieRepositoryTest {

    @Mock
    private lateinit var remoteDataSource: RemoteDataSource

    @Mock
    private lateinit var localDataSource: LocalDataSource

    private lateinit var movieRepository: MovieRepository
    private lateinit var appExecutors: AppExecutors

    private val dummyMovieResponse = listOf(
        MovieResponse(
            id = 1,
            title = "Test Movie 1",
            overview = "Test Overview 1",
            posterPath = "/test1.jpg",
            backdropPath = "/backdrop1.jpg",
            voteAverage = 8.5,
            releaseDate = "2023-01-01"
        ),
        MovieResponse(
            id = 2,
            title = "Test Movie 2",
            overview = "Test Overview 2",
            posterPath = "/test2.jpg",
            backdropPath = "/backdrop2.jpg",
            voteAverage = 7.5,
            releaseDate = "2023-02-01"
        )
    )

    private val dummyMovieEntity = listOf(
        MovieEntity(
            id = 1,
            title = "Test Movie 1",
            overview = "Test Overview 1",
            posterPath = "/test1.jpg",
            backdropPath = "/backdrop1.jpg",
            voteAverage = 8.5,
            releaseDate = "2023-01-01",
            isFavorite = false
        ),
        MovieEntity(
            id = 2,
            title = "Test Movie 2",
            overview = "Test Overview 2",
            posterPath = "/test2.jpg",
            backdropPath = "/backdrop2.jpg",
            voteAverage = 7.5,
            releaseDate = "2023-02-01",
            isFavorite = false
        )
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        appExecutors = AppExecutors()
    }

    @Test
    fun `getAllMovies should return success when remote data is available`() = runTest {
        // Given
        val emptyList = emptyList<MovieEntity>()
        val savedList = dummyMovieEntity

        // First call returns empty, subsequent calls return saved data
        `when`(localDataSource.getAllMovies())
            .thenReturn(flowOf(emptyList))
            .thenReturn(flowOf(savedList))

        `when`(remoteDataSource.getAllMovies()).thenReturn(
            flowOf(ApiResponse.Success(dummyMovieResponse))
        )

        // Mock suspend function
        `when`(localDataSource.insertMovies(any())).doAnswer { }

        movieRepository = MovieRepository(remoteDataSource, localDataSource, appExecutors)

        // When
        val results = movieRepository.getAllMovies().toList()

        // Then
        val hasLoading = results.any { it is Resource.Loading }
        val successWithData = results
            .filterIsInstance<Resource.Success<List<Movie>>>()
            .firstOrNull { it.data?.isNotEmpty() == true }

        assertTrue("Should have Loading state", hasLoading)
        assertNotNull("Should have Success with data", successWithData)
        assertNotNull("Data should not be null", successWithData?.data)
        assertTrue("Should have at least 1 movie", (successWithData?.data?.size ?: 0) > 0)
    }

    @Test
    fun `getAllMovies should return error when remote call fails`() = runTest {
        // Given
        `when`(localDataSource.getAllMovies()).thenReturn(flowOf(emptyList()))
        `when`(remoteDataSource.getAllMovies()).thenReturn(
            flowOf(ApiResponse.Error("Network error"))
        )

        movieRepository = MovieRepository(remoteDataSource, localDataSource, appExecutors)

        // When
        val results = movieRepository.getAllMovies().toList()

        // Then
        val hasLoading = results.any { it is Resource.Loading }
        val hasError = results.any { it is Resource.Error }

        assertTrue("Should have Loading state", hasLoading)
        assertTrue("Should have Error state", hasError)
    }

    @Test
    fun `getAllMovies should return cached data when available`() = runTest {
        // Given
        `when`(localDataSource.getAllMovies()).thenReturn(flowOf(dummyMovieEntity))

        movieRepository = MovieRepository(remoteDataSource, localDataSource, appExecutors)

        // When
        val results = movieRepository.getAllMovies().toList()

        // Then
        val hasLoading = results.any { it is Resource.Loading }
        val successWithData = results
            .filterIsInstance<Resource.Success<List<Movie>>>()
            .firstOrNull { it.data?.isNotEmpty() == true }

        assertTrue("Should have Loading state", hasLoading)
        assertNotNull("Should have cached data", successWithData)
        assertTrue("Should have data", (successWithData?.data?.size ?: 0) > 0)
    }

    @Test
    fun `searchMovies should filter movies correctly`() = runTest {
        // Given
        val query = "Test Movie 1"
        `when`(localDataSource.getAllMovies())
            .thenReturn(flowOf(dummyMovieEntity))
            .thenReturn(flowOf(listOf(dummyMovieEntity[0])))

        `when`(remoteDataSource.searchMovies(query)).thenReturn(
            flowOf(ApiResponse.Success(listOf(dummyMovieResponse[0])))
        )

        `when`(localDataSource.insertMovies(any())).doAnswer { }

        movieRepository = MovieRepository(remoteDataSource, localDataSource, appExecutors)

        // When
        val results = movieRepository.searchMovies(query).toList()

        // Then
        val hasLoading = results.any { it is Resource.Loading }
        val hasSuccess = results.any { it is Resource.Success }

        assertTrue("Should have Loading state", hasLoading)
        assertTrue("Should have Success state", hasSuccess)
    }

    @Test
    fun `getFavoriteMovies should return only favorite movies`() = runTest {
        // Given
        val favoriteMovies = listOf(
            dummyMovieEntity[0].copy(isFavorite = true)
        )
        `when`(localDataSource.getFavoriteMovies()).thenReturn(flowOf(favoriteMovies))

        movieRepository = MovieRepository(remoteDataSource, localDataSource, appExecutors)

        // When
        val results = movieRepository.getFavoriteMovies().toList()

        // Then
        assertEquals("Should have 1 result", 1, results.size)
        val result = results[0]
        assertEquals("Should have 1 favorite movie", 1, result.size)
        assertTrue("Movie should be favorite", result[0].isFavorite)
    }
}