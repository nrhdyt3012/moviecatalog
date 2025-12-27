package com.dicoding.moviecatalog.core.domain.usecase

import app.cash.turbine.test
import com.dicoding.moviecatalog.core.data.Resource
import com.dicoding.moviecatalog.core.domain.model.Movie
import com.dicoding.moviecatalog.core.domain.repository.IMovieRepository
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

@ExperimentalCoroutinesApi
class MovieInteractorTest {

    @Mock
    private lateinit var movieRepository: IMovieRepository

    private lateinit var movieInteractor: MovieInteractor

    private val dummyMovies = listOf(
        Movie(
            id = 1,
            title = "Test Movie",
            overview = "Test Overview",
            posterPath = "/test.jpg",
            backdropPath = "/backdrop.jpg",
            voteAverage = 8.5,
            releaseDate = "2023-01-01",
            isFavorite = false
        )
    )

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        movieInteractor = MovieInteractor(movieRepository)
    }

    @Test
    fun `getAllMovies should return flow from repository`() = runTest {
        // Given
        `when`(movieRepository.getAllMovies()).thenReturn(
            flowOf(Resource.Success(dummyMovies))
        )

        // When & Then
        movieInteractor.getAllMovies().test {
            val result = awaitItem()
            assertTrue(result is Resource.Success)
            assertEquals(1, result.data?.size)

            awaitComplete()
        }

        verify(movieRepository).getAllMovies()
    }

    @Test
    fun `searchMovies should call repository with correct query`() = runTest {
        // Given
        val query = "Test"
        `when`(movieRepository.searchMovies(query)).thenReturn(
            flowOf(Resource.Success(dummyMovies))
        )

        // When & Then
        movieInteractor.searchMovies(query).test {
            val result = awaitItem()
            assertTrue(result is Resource.Success)

            awaitComplete()
        }

        verify(movieRepository).searchMovies(query)
    }

    @Test
    fun `setFavoriteMovie should call repository`() {
        // Given
        val movie = dummyMovies[0]
        val state = true

        // When
        movieInteractor.setFavoriteMovie(movie, state)

        // Then
        verify(movieRepository).setFavoriteMovie(movie, state)
    }
}
