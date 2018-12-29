package com.varomovie.varomovie.db.converters

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.varomovie.varomovie.models.MovieObject

class VaroMovieConverter {
    private val gson = Gson()

    @TypeConverter
    fun jsonFromMovieList(expanded: List<MovieObject>?) : String? {
        return gson.toJson(expanded)
    }

    @TypeConverter
    fun expandedMovieListFromJson(json: String?) : List<MovieObject>? {
        return gson.fromJson(json, object : TypeToken<List<MovieObject>>(){}.type)
    }
}