package com.varomovie.varomovie.dagger

import com.varomovie.varomovie.Api
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiModule {
    fun provideNetService(): Api {
        return Retrofit
            .Builder()
            .client(OkHttpClient())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(Api::class.java)
    }

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
    }
}