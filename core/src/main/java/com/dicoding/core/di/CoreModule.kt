package com.dicoding.moviecatalog.core.di

import androidx.room.Room
import com.dicoding.moviecatalog.core.BuildConfig
import com.dicoding.moviecatalog.core.data.MovieRepository
import com.dicoding.moviecatalog.core.data.source.local.LocalDataSource
import com.dicoding.moviecatalog.core.data.source.local.room.MovieDatabase
import com.dicoding.moviecatalog.core.data.source.remote.RemoteDataSource
import com.dicoding.moviecatalog.core.data.source.remote.network.ApiService
import com.dicoding.moviecatalog.core.domain.repository.IMovieRepository
import com.dicoding.moviecatalog.core.utils.AppExecutors
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val databaseModule = module {
    factory { get<MovieDatabase>().movieDao() }
    single {
        val passphrase: ByteArray = SQLiteDatabase.getBytes("dicoding".toCharArray())
        val factory = SupportFactory(passphrase)
        Room.databaseBuilder(
            androidContext(),
            MovieDatabase::class.java, "Movie.db"
        ).fallbackToDestructiveMigration()
            .openHelperFactory(factory)
            .build()
    }
}

val networkModule = module {
    single {
        val hostname = "api.themoviedb.org"

        // UPDATE: Hash sertifikat baru berdasarkan error message
        val certificatePinner = CertificatePinner.Builder()
            // Current certificate for *.themoviedb.org
            .add(hostname, "sha256/f78NVAesYtdZ9OGSbK7VtGQkSIVykh3DnduuLIJHMu4=")
            // Amazon RSA 2048 M04 (Intermediate CA)
            .add(hostname, "sha256/G9LNNAq1897egYsabashkzUCTEJkWBzgoEtk8X/678c=")
            // Amazon Root CA 1 (Root CA)
            .add(hostname, "sha256/++MBgDH5WGvL9Bcn5Be30cRcL0f50+NyoXuWtQdX1al=")
            .build()

        val authInterceptor = Interceptor { chain ->
            val req = chain.request()
            val requestHeaders = req.newBuilder()
                .addHeader("Authorization", "Bearer ${BuildConfig.API_KEY}")
                .build()
            chain.proceed(requestHeaders)
        }

        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .certificatePinner(certificatePinner)
            .build()
    }
    single {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(get())
            .build()
        retrofit.create(ApiService::class.java)
    }
}

val repositoryModule = module {
    single { LocalDataSource(get()) }
    single { RemoteDataSource(get()) }
    factory { AppExecutors() }
    single<IMovieRepository> {
        MovieRepository(
            get(),
            get(),
            get()
        )
    }
}