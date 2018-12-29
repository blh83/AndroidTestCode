package com.varomovie.varomovie

import android.graphics.Bitmap
import com.squareup.picasso.Picasso
import com.varomovie.varomovie.db.VaroMovieDao
import com.varomovie.varomovie.models.MovieDbObject
import com.varomovie.varomovie.models.MovieObject
import com.varomovie.varomovie.models.MovieResponseObject
import io.reactivex.Single

class VaroMovieRepository(private val api: Api, private val varoMovieDao: VaroMovieDao) {

    fun getMovies(page: Int) : Single<MovieResponseObject> {
        return api.getMovies(page)
    }

    fun getMoviePoster(posterPath: String) : Bitmap {
        return Picasso.get().load(String.format("%s%s", IMAGE_BASE_URL, posterPath)).get()
    }

    fun insertFavoritesToDb(favoriteMovies: List<MovieObject?>) {
        varoMovieDao.cleanInsertFavorites(MovieDbObject(0, favoriteMovies))
    }

    fun getFavorites() : MovieDbObject{
        return varoMovieDao.getFavoritesList()
    }

    fun insertMoviesIntoDb(movies: List<MovieObject?>, pageId: Int) {
        varoMovieDao.cleanInsertFavorites(MovieDbObject(pageId, movies))
    }

    fun getCachedMovies(pageId: Int) : MovieDbObject{
        return varoMovieDao.getMoviesByPage(pageId)
    }

    companion object {
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
    }
}