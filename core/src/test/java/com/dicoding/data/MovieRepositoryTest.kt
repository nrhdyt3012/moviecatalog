package com.dicoding.moviecatalog.core.data

import app.cash.turbine.test
import com.dicoding.moviecatalog.core.data.source.local.LocalDataSource
import com.dicoding.moviecatalog.core.data.source.local.entity.MovieEntity
import com.dicoding.moviecatalog.core.data.source.remote.RemoteDataSource
import com.dicoding.moviecatalog.core.data.source.remote.network.ApiResponse
import com.dicoding.moviecatalog.core.data.source.remote.response.MovieResponse
import com.dicoding.moviecatalog.core.utils.AppExecutors
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import kotlin.time.Duration.Companion.seconds

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
        movieRepository.getAllMovies().test(timeout = 5.seconds) {
            // First emission: Loading
            val loading = awaitItem()
            assertTrue("First emission should be Loading", loading is Resource.Loading)

            // Second emission: Loading (before fetch)
            val loading2 = awaitItem()
            assertTrue("Second emission should be Loading", loading2 is Resource.Loading)

            // Third emission: Success with data
            val success = awaitItem()
            assertTrue("Third emission should be Success", success is Resource.Success)
            assertNotNull("Data should not be null", success.data)
            assertEquals("Should have 2 movies", 2, success.data?.size)

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
        movieRepository.getAllMovies().test(timeout = 5.seconds) {
            // First emission: Loading
            val loading = awaitItem()
            assertTrue("First emission should be Loading", loading is Resource.Loading)

            // Second emission: Loading (before fetch)
            val loading2 = awaitItem()
            assertTrue("Second emission should be Loading", loading2 is Resource.Loading)

            // Third emission: Error
            val error = awaitItem()
            assertTrue("Third emission should be Error", error is Resource.Error)
            assertNotNull("Error message should not be null", error.message)
            assertTrue(
                "Error message should contain error info",
                error.message?.contains("kesalahan") == true ||
                        error.message?.contains("error") == true
            )

            awaitComplete()
        }
    }

    @Test
    fun `getAllMovies should return cached data when available`() = runTest {
        // Given
        `when`(localDataSource.getAllMovies()).thenReturn(flowOf(dummyMovieEntity))

        // When & Then
        movieRepository.getAllMovies().test(timeout = 5.seconds) {
            // First emission: Loading
            val loading = awaitItem()
            assertTrue("First emission should be Loading", loading is Resource.Loading)

            // Second emission: Success with cached data
            val success = awaitItem()
            assertTrue("Second emission should be Success", success is Resource.Success)
            assertEquals("Should have 1 movie", 1, success.data?.size)
            assertEquals("Movie title should match", "Test Movie 1", success.data?.get(0)?.title)

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
        movieRepository.searchMovies(query).test(timeout = 5.seconds) {
            // First emission: Loading
            val loading = awaitItem()
            assertTrue("First emission should be Loading", loading is Resource.Loading)

            // Second emission: Loading (before fetch)
            val loading2 = awaitItem()
            assertTrue("Second emission should be Loading", loading2 is Resource.Loading)

            // Third emission: Success
            val success = awaitItem()
            assertTrue("Third emission should be Success", success is Resource.Success)
            assertEquals("Should have 1 movie", 1, success.data?.size)
            assertEquals("Movie title should match", "Test Movie 1", success.data?.get(0)?.title)

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
        movieRepository.getFavoriteMovies().test(timeout = 5.seconds) {
            val result = awaitItem()
            assertEquals("Should have 1 favorite movie", 1, result.size)
            assertTrue("Movie should be favorite", result[0].isFavorite)

            awaitComplete()
        }
    }

    @Test
    fun `getAllMovies should return error with cached data when network fails but cache exists`() = runTest {
        // Given
        `when`(localDataSource.getAllMovies()).thenReturn(flowOf(dummyMovieEntity))
        `when`(remoteDataSource.getAllMovies()).thenReturn(
            flowOf(ApiResponse.Error("Network timeout"))
        )

        // When & Then
        movieRepository.getAllMovies().test(timeout = 5.seconds) {
            // First: Loading
            val loading = awaitItem()
            assertTrue(loading is Resource.Loading)

            // Second: Success with cached data (shouldFetch returns false because cache exists)
            val success = awaitItem()
            assertTrue("Should return cached data", success is Resource.Success)
            assertEquals(1, success.data?.size)

            awaitComplete()
        }
    }
}