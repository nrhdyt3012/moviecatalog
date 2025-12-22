package com.dicoding.moviecatalog.core.data.source.remote

import android.util.Log
import com.dicoding.moviecatalog.core.data.source.remote.network.ApiResponse
import com.dicoding.moviecatalog.core.data.source.remote.network.ApiService
import com.dicoding.moviecatalog.core.data.source.remote.response.MovieResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

class RemoteDataSource(private val apiService: ApiService) {

    suspend fun getAllMovies(): Flow<ApiResponse<List<MovieResponse>>> {
        return flow {
            try {
                val response = apiService.getPopularMovies()
                val dataArray = response.results
                if (dataArray.isNotEmpty()) {
                    emit(ApiResponse.Success(dataArray))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                val errorMessage = getErrorMessage(e)
                emit(ApiResponse.Error(errorMessage))
                Log.e("RemoteDataSource", "getAllMovies error: $errorMessage", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    suspend fun searchMovies(query: String): Flow<ApiResponse<List<MovieResponse>>> {
        return flow {
            try {
                val response = apiService.searchMovies(query)
                val dataArray = response.results
                if (dataArray.isNotEmpty()) {
                    emit(ApiResponse.Success(dataArray))
                } else {
                    emit(ApiResponse.Empty)
                }
            } catch (e: Exception) {
                val errorMessage = getErrorMessage(e)
                emit(ApiResponse.Error(errorMessage))
                Log.e("RemoteDataSource", "searchMovies error: $errorMessage", e)
            }
        }.flowOn(Dispatchers.IO)
    }

    private fun getErrorMessage(exception: Exception): String {
        return when (exception) {
            is UnknownHostException -> {
                "UnknownHostException: Tidak dapat terhubung ke server. Periksa koneksi internet Anda."
            }
            is SocketTimeoutException -> {
                "SocketTimeoutException: Koneksi timeout. Server tidak merespons."
            }
            is IOException -> {
                "IOException: Masalah koneksi jaringan. ${exception.message}"
            }
            is HttpException -> {
                when (exception.code()) {
                    401 -> "HTTP 401: Akses tidak diizinkan."
                    403 -> "HTTP 403: Akses ditolak."
                    404 -> "HTTP 404: Data tidak ditemukan."
                    500 -> "HTTP 500: Server sedang bermasalah."
                    503 -> "HTTP 503: Server tidak tersedia."
                    else -> "HTTP ${exception.code()}: ${exception.message()}"
                }
            }
            is SSLException -> {
                "SSLException: Masalah keamanan koneksi. ${exception.message}"
            }
            else -> {
                "Error: ${exception.javaClass.simpleName} - ${exception.message}"
            }
        }
    }
}