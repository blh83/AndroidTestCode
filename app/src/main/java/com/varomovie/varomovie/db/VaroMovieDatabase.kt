package com.varomovie.varomovie.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.varomovie.varomovie.db.converters.VaroMovieConverter
import com.varomovie.varomovie.models.MovieDbObject

@Database(entities = [MovieDbObject::class], version = 1, exportSchema = false)
@TypeConverters(
    VaroMovieConverter::class
)
abstract  class VaroMovieDatabase : RoomDatabase(){
    abstract fun varoMovieDao(): VaroMovieDao
}