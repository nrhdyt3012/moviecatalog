package com.dicoding.moviecatalog.core.data

import com.dicoding.moviecatalog.core.data.source.remote.network.ApiResponse
import kotlinx.coroutines.flow.*

abstract class NetworkBoundResource<ResultType, RequestType> {

    private var result: Flow<Resource<ResultType>> = flow {
        emit(Resource.Loading())
        val dbSource = loadFromDB().first()

        if (shouldFetch(dbSource)) {
            emit(Resource.Loading())

            when (val apiResponse = createCall().first()) {
                is ApiResponse.Success -> {
                    saveCallResult(apiResponse.data)
                    emitAll(loadFromDB().map { Resource.Success(it) })
                }
                is ApiResponse.Empty -> {
                    emitAll(loadFromDB().map { Resource.Success(it) })
                }
                is ApiResponse.Error -> {
                    onFetchFailed()
                    val errorMessage = parseErrorMessage(apiResponse.errorMessage)

                    // Jika ada data di database, tampilkan data tersebut dengan pesan error
                    if (dbSource != null && isDataNotEmpty(dbSource)) {
                        emit(Resource.Error(errorMessage, dbSource))
                    } else {
                        emit(Resource.Error<ResultType>(errorMessage))
                    }
                }
            }
        } else {
            emitAll(loadFromDB().map { Resource.Success(it) })
        }
    }

    private fun parseErrorMessage(error: String): String {
        return when {
            error.contains("Unable to resolve host", ignoreCase = true) ||
                    error.contains("UnknownHostException", ignoreCase = true) ->
                "Tidak ada koneksi internet. Silakan periksa koneksi Anda."

            error.contains("timeout", ignoreCase = true) ->
                "Koneksi timeout. Silakan coba lagi."

            error.contains("SocketTimeoutException", ignoreCase = true) ->
                "Server tidak merespons. Silakan coba lagi."

            error.contains("ConnectException", ignoreCase = true) ->
                "Gagal terhubung ke server. Periksa koneksi internet Anda."

            error.contains("SSLException", ignoreCase = true) ||
                    error.contains("CertPathValidatorException", ignoreCase = true) ->
                "Masalah keamanan koneksi. Silakan coba lagi."

            error.contains("HTTP 404", ignoreCase = true) ->
                "Data tidak ditemukan."

            error.contains("HTTP 500", ignoreCase = true) ->
                "Server sedang bermasalah. Silakan coba lagi nanti."

            error.contains("HTTP 401", ignoreCase = true) ||
                    error.contains("HTTP 403", ignoreCase = true) ->
                "Akses ditolak. Silakan coba lagi."

            else -> "Terjadi kesalahan. Silakan coba lagi."
        }
    }

    private fun isDataNotEmpty(data: ResultType): Boolean {
        return when (data) {
            is List<*> -> data.isNotEmpty()
            else -> data != null
        }
    }

    protected open fun onFetchFailed() {}

    protected abstract fun loadFromDB(): Flow<ResultType>

    protected abstract fun shouldFetch(data: ResultType?): Boolean

    protected abstract suspend fun createCall(): Flow<ApiResponse<RequestType>>

    protected abstract suspend fun saveCallResult(data: RequestType)

    fun asFlow(): Flow<Resource<ResultType>> = result
}