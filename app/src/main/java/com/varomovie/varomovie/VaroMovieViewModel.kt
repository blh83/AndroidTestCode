package com.varomovie.varomovie

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import com.varomovie.varomovie.models.MovieDbObject
import com.varomovie.varomovie.models.MovieObject
import com.varomovie.varomovie.models.MovieResponseObject
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class VaroMovieViewModel(private val repository: VaroMovieRepository) : ViewModel() {

    val state: MutableLiveData<VaroMovieViewModelStatus> = MutableLiveData()
    enum class Status {MOVIES, MOVIE_POSTER, FAVORITES_LIST, FAVORITES_ERROR, CACHED_MOVIES, CACHED_ERROR, ERROR}

    data class VaroMovieViewModelStatus(val status: Status, val movies: MovieResponseObject? = null,
                                        val error: Throwable? = null, val moviePoster: Bitmap? = null,
                                        val listPosition: Int? = null, val favorites: MovieDbObject? = null,
                                        val isFavorite: Boolean? = false, val cachedMovies: MovieDbObject? = null,
                                        val pageId: Int? = null) {
        companion object {
            fun showError(error: Throwable?) = VaroMovieViewModelStatus(Status.ERROR, error = error)
            fun favoritesError() = VaroMovieViewModelStatus(Status.FAVORITES_ERROR)
            fun cachedError(pageId: Int?) = VaroMovieViewModelStatus(Status.CACHED_ERROR, pageId = pageId)
            fun showMovies(movies: MovieResponseObject?) = VaroMovieViewModelStatus(Status.MOVIES, movies = movies)
            fun showCachedMovies(movies: MovieDbObject?) = VaroMovieViewModelStatus(Status.CACHED_MOVIES, cachedMovies = movies)
            fun showMoviePoster(moviePoster: Bitmap?, listPosition: Int?, isFavorite: Boolean?) =
                VaroMovieViewModelStatus(Status.MOVIE_POSTER, moviePoster = moviePoster, listPosition = listPosition, isFavorite = isFavorite)
            fun getFavorites(favorites: MovieDbObject?) = VaroMovieViewModelStatus(Status.FAVORITES_LIST, favorites = favorites)
        }
    }

    fun getMovies(page: Int) {
            repository.getMovies(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ movies ->
                    state.value = VaroMovieViewModelStatus.showMovies(movies)
                },{state.value = VaroMovieViewModelStatus.showError (it)})
    }

    fun getMoviePoster(posterPath: String, position: Int, isFavorite: Boolean?) {
        Single.fromCallable {
            repository.getMoviePoster(posterPath)
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ moviePoster ->
                state.value = VaroMovieViewModelStatus.showMoviePoster(moviePoster, position, isFavorite)
            },{state.value = VaroMovieViewModelStatus.showError (it)})
    }

    fun saveFavoritesToDb(favorites: List<MovieObject?>) {
        Single.fromCallable { repository.insertFavoritesToDb(favorites) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun getFavoriteMovies() {
        Single.fromCallable { repository.getFavorites() }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                state.value = VaroMovieViewModelStatus.getFavorites(it)
            },{ error ->
                state.value = VaroMovieViewModelStatus.favoritesError()})
    }

    fun saveMovieListToDb(movies: List<MovieObject?>, pageId: Int) {
        Single.fromCallable { repository.insertMoviesIntoDb(movies, pageId) }
            .subscribeOn(Schedulers.io())
            .subscribe({
                val g = it
            },{
                val garbage = it
            })
    }

    fun getMoviesByPage(pageId: Int) {
        Single.fromCallable { repository.getCachedMovies(pageId) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                state.value = VaroMovieViewModelStatus.showCachedMovies(it)
            },{ error ->
                state.value = VaroMovieViewModelStatus.cachedError(pageId)})
    }
}