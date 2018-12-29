package com.varomovie.varomovie

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.varomovie.varomovie.models.MovieObject
import kotlinx.android.synthetic.main.adapter_movie_list_item.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MovieListAdapter(private val movieList: MutableList<MovieObject?>, private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val singleDecimalFormat = DecimalFormat("0.0")
    private val humanReadableDate = "MMM dd, yyyy"
    private val yearMonthDay= "yyyy-mm-dd"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_movie_list_item, parent, false)
        return MovieViewHolder(view)
    }

    override fun getItemCount(): Int {
        return movieList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as MovieViewHolder
        val movie = movieList[position]

        viewHolder.movieTitle.text = movie?.title ?: context.getString(R.string.title_error)
        viewHolder.rating.text = String.format(context.getString(R.string.movie_popularity),
            singleDecimalFormat.format(movie?.popularity).toString())
        viewHolder.releaseDate.text = String.format(context.getString(R.string.movie_release_date),
            SimpleDateFormat(humanReadableDate, Locale.US).format(SimpleDateFormat(yearMonthDay,Locale.US).parse(movie?.releaseDate)))
        viewHolder.moviePoster.setImageDrawable(null)
        viewHolder.moviePoster.setImageBitmap(movie?.moviePoster)
        if(movie?.isFavorite == true) {
            viewHolder.favoriteHeart.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_selected))
        } else {
            viewHolder.favoriteHeart.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border))
        }
    }

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var movieTitle: TextView = itemView.findViewById(R.id.movieTitle)
        var releaseDate: TextView = itemView.findViewById(R.id.movieReleaseDate)
        var rating: TextView = itemView.findViewById(R.id.movieRating)
        var moviePoster: ImageView = itemView.findViewById(R.id.moviePoster)
        var favoriteHeart: ImageView = itemView.findViewById(R.id.favoriteHeart)
        var movieDetail: ImageView = itemView.findViewById(R.id.movieDetails)

        init {
            favoriteHeart.setOnClickListener {
                val position = adapterPosition
                val movie = movieList[position]
                if(movie?.isFavorite == true) {
                    itemView.favoriteHeart.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_border))
                    movie.isFavorite = false
                    (context as VaroMovieActivity).removeFromFavorites(movie, position)
                } else {
                    itemView.favoriteHeart.setImageDrawable(context.getDrawable(R.drawable.ic_favorite_selected))
                    movie?.isFavorite = true
                    (context as VaroMovieActivity).addToFavorites(movie!!)
                }
            }

            movieDetail.setOnClickListener {
                val position = adapterPosition
                // TODO: Yuck! Because the moviePoster isn't being ignored on parcelize (like it should) I have to set it
                // to null. However, if you click on the details, come back, favorite/un-favorite a movie, then this movie
                // will lose its poster image if I don't do this copy.
                val movie = movieList[position]?.copy()
                movie?.moviePoster = null

                var options: ActivityOptions?
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    options = ActivityOptions.makeSceneTransitionAnimation(
                        context as Activity,
                        itemView.moviePoster,
                        context.getString(R.string.transition_name)
                    )
                    val intent = Intent(context, MovieDetailPage::class.java)
                    intent.putExtra(MovieObject.MOVIE_OBJECT_INTENT, movie)
                    context.startActivity(intent, options?.toBundle())
                }
            }
        }
    }
}