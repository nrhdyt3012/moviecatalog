package com.dicoding.moviecatalog.di

import com.dicoding.moviecatalog.core.domain.usecase.MovieInteractor
import com.dicoding.moviecatalog.core.domain.usecase.MovieUseCase
import com.dicoding.moviecatalog.ui.detail.DetailMovieViewModel
import com.dicoding.moviecatalog.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<MovieUseCase> { MovieInteractor(get()) }
}

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { DetailMovieViewModel(get()) }
}