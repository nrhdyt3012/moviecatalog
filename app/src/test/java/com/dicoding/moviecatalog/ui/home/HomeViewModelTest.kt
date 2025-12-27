package com.dicoding.moviecatalog.ui.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.dicoding.moviecatalog.core.data.Resource
import com.dicoding.moviecatalog.core.domain.model.Movie
import com.dicoding.moviecatalog.core.domain.usecase.MovieUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class HomeViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var movieUseCase: MovieUseCase

    private lateinit var viewModel: HomeViewModel

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
        `when`(movieUseCase.getAllMovies()).thenReturn(
            flowOf(Resource.Success(dummyMovies))
        )
        viewModel = HomeViewModel(movieUseCase)
    }

    @Test
    fun `movies LiveData should not be null`() {
        assertNotNull(viewModel.movies)
    }

    @Test
    fun `searchMovies should return filtered results`() = runTest {
        // Given
        val query = "Test"
        `when`(movieUseCase.searchMovies(query)).thenReturn(
            flowOf(Resource.Success(dummyMovies))
        )

        // When
        val result = viewModel.searchMovies(query)

        // Then
        assertNotNull(result)
    }
}