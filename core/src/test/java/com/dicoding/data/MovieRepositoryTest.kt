package com.dicoding.moviecatalog.core.data

import app.cash.turbine.test
import com.dicoding.moviecatalog.core.data.source.local.LocalDataSource
import com.dicoding.moviecatalog.core.data.source.local.entity.MovieEntity
import com.dicoding.moviecatalog.core.data.source.remote.RemoteDataSource
import com.dicoding.moviecatalog.core.data.source.remote.network.ApiResponse
import com.dicoding.moviecatalog.core.data.source.remote.response.MovieResponse
import com.dicoding.moviecatalog.core.utils.AppExecutors
import com.dicoding.moviecatalog.core.utils.DataMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import java.util.concurrent.Executor

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
        )
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        appExecutors = AppExecutors()
        movieRepository = MovieRepository(remoteDataSource, localDataSource, appExecutors)
    }

    @Test
    fun `getAllMovies should return success when remote data is available`() = runTest {
        // Given
        `when`(localDataSource.getAllMovies()).thenReturn(flowOf(emptyList()))
        `when`(remoteDataSource.getAllMovies()).thenReturn(
            flowOf(ApiResponse.Success(dummyMovieResponse))
        )

        // When & Then
        movieRepository.getAllMovies().test {
            // Loading state
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            // Success state
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertNotNull(success.data)
            assertEquals(2, success.data?.size)

            awaitComplete()
        }
    }

    @Test
    fun `getAllMovies should return error when remote call fails`() = runTest {
        // Given
        `when`(localDataSource.getAllMovies()).thenReturn(flowOf(emptyList()))
        `when`(remoteDataSource.getAllMovies()).thenReturn(
            flowOf(ApiResponse.Error("Network error"))
        )

        // When & Then
        movieRepository.getAllMovies().test {
            // Loading state
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            // Error state
            val error = awaitItem()
            assertTrue(error is Resource.Error)
            assertNotNull(error.message)

            awaitComplete()
        }
    }

    @Test
    fun `getAllMovies should return cached data when available`() = runTest {
        // Given
        `when`(localDataSource.getAllMovies()).thenReturn(flowOf(dummyMovieEntity))

        // When & Then
        movieRepository.getAllMovies().test {
            // Loading state
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            // Success state with cached data
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(1, success.data?.size)
            assertEquals("Test Movie 1", success.data?.get(0)?.title)

            awaitComplete()
        }
    }

    @Test
    fun `searchMovies should filter movies correctly`() = runTest {
        // Given
        val query = "Test Movie 1"
        `when`(localDataSource.getAllMovies()).thenReturn(flowOf(dummyMovieEntity))
        `when`(remoteDataSource.searchMovies(query)).thenReturn(
            flowOf(ApiResponse.Success(listOf(dummyMovieResponse[0])))
        )

        // When & Then
        movieRepository.searchMovies(query).test {
            // Loading state
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            // Success state
            val success = awaitItem()
            assertTrue(success is Resource.Success)
            assertEquals(1, success.data?.size)
            assertEquals("Test Movie 1", success.data?.get(0)?.title)

            awaitComplete()
        }
    }

    @Test
    fun `getFavoriteMovies should return only favorite movies`() = runTest {
        // Given
        val favoriteMovies = listOf(
            dummyMovieEntity[0].copy(isFavorite = true)
        )
        `when`(localDataSource.getFavoriteMovies()).thenReturn(flowOf(favoriteMovies))

        // When & Then
        movieRepository.getFavoriteMovies().test {
            val result = awaitItem()
            assertEquals(1, result.size)
            assertTrue(result[0].isFavorite)

            awaitComplete()
        }
    }
}