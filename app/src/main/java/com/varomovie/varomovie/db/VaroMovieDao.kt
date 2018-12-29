package com.varomovie.varomovie.db

import android.arch.persistence.room.*
import com.varomovie.varomovie.models.MovieDbObject

@Dao
abstract class VaroMovieDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertFavoritesList(favorites: MovieDbObject)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMovieList(list: MovieDbObject)

    @Query("SELECT * FROM movie_db_object WHERE pageId = 0") // There is no page zero, so use it for favorites id
    abstract fun getFavoritesList() : MovieDbObject

    @Query("SELECT * FROM movie_db_object WHERE pageId = :pageId") // There is no page zero, so use it for favorites id
    abstract fun getMoviesByPage(pageId: Int) : MovieDbObject

    @Query("DELETE FROM movie_db_object WHERE pageId = 0")
    abstract fun deleteFavorites()

    @Transaction
    open fun cleanInsertFavorites(favorites: MovieDbObject) {
        deleteFavorites()
        insertFavoritesList(favorites)
    }
}