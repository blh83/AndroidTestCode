package com.varomovie.varomovie

import com.varomovie.varomovie.models.MovieResponseObject
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {
    @GET("movie/now_playing?language=en-us&api_key=7bfe007798875393b05c5aa1ba26323e")
    fun getMovies(@Query("page") page: Int): Single<MovieResponseObject>
}