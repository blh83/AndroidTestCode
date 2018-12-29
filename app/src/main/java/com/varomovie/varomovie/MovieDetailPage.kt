package com.varomovie.varomovie

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.squareup.picasso.Picasso
import com.varomovie.varomovie.models.MovieObject
import kotlinx.android.synthetic.main.activity_movie_detail.*

class MovieDetailPage : AppCompatActivity() {

    private val imageBaseUrl= "https://image.tmdb.org/t/p/w780"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)
        val movie: MovieObject?

        if(intent != null) {
            if(intent.hasExtra(MovieObject.MOVIE_OBJECT_INTENT)) {
                movie = intent.getParcelableExtra(MovieObject.MOVIE_OBJECT_INTENT)
                Picasso.get().load(String.format("%s%s", imageBaseUrl, movie?.posterPath)).into(moviePoster)
                movieDescription.text = movie?.overview
            }
        }
    }
}