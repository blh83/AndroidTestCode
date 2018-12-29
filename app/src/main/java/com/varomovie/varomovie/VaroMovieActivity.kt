package com.varomovie.varomovie

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.support.v7.widget.SearchView.OnQueryTextListener
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.varomovie.varomovie.models.MovieObject
import com.varomovie.varomovie.models.VaroViewModelFactory

class VaroMovieActivity : AppCompatActivity() {

    private lateinit var viewModel: VaroMovieViewModel
    private var movieList: MutableList<MovieObject?> = mutableListOf()
    private var favoriteMovies: MutableList<MovieObject?> = mutableListOf()
    private var searchResults: MutableList<MovieObject?> = mutableListOf()
    private lateinit var movieListRecyclerView: RecyclerView
    private lateinit var movieListAdapter: MovieListAdapter
    private var currentPage = 1
    private var showingFavorites = false
    private var searching = false
    private var loading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_varo_movie)
        setSupportActionBar(findViewById(R.id.toolbar))

        init()

        val layoutManager = object : GridLayoutManager(applicationContext, 2) {}
        movieListRecyclerView = findViewById(R.id.movieRecycleList)
        movieListRecyclerView.layoutManager = layoutManager
        movieListAdapter = MovieListAdapter(movieList, this)
        movieListRecyclerView.adapter = movieListAdapter

        movieListRecyclerView.addOnScrollListener(
            (object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // This is not the best infinite scroll in the world but it works okay
                    val lastItem = layoutManager.findLastCompletelyVisibleItemPosition()
                    val totalItemCount = recyclerView?.layoutManager?.itemCount

                    if (!showingFavorites && !searching && totalItemCount!! <= lastItem + 3 && !loading) {
                        loading = true
                        currentPage++
                        viewModel.getMoviesByPage(currentPage)
                        Toast.makeText(this@VaroMovieActivity, "Loading more movies...", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        )
    }

    private fun init() {
        initViewModel()
        initObserver()
        viewModel.getFavoriteMovies()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, VaroViewModelFactory(this)).get(VaroMovieViewModel::class.java)
    }

    private fun initObserver() {
        viewModel.state.observe(this, Observer<VaroMovieViewModel.VaroMovieViewModelStatus> {status ->
            when(status?.status) {
                VaroMovieViewModel.Status.ERROR -> {
                    Toast.makeText(this, status.error?.message, Toast.LENGTH_LONG).show()
                    loading = false
                }
                VaroMovieViewModel.Status.MOVIES -> {
                    val movies = status.movies
                    for(mov in movies?.results ?: mutableListOf()) {
                        if(favoriteMovies.filter { it?.id == mov?.id }.isNotEmpty() ) {
                            mov?.isFavorite = true
                        }
                       movieList.add(mov)
                    }

                    if(status.movies?.results != null) {
                        viewModel.saveMovieListToDb(status.movies.results!!, currentPage)
                    }

                    for(movie in movieList) {
                        movie?.posterPath?.let {
                            viewModel.getMoviePoster(it, movieList.indexOf(movie), false)
                        } // we don't know the poster path (or there was an error) so don't bother trying to retrieve it
                    }

                    loading = false
                }
                VaroMovieViewModel.Status.MOVIE_POSTER -> {
                    val poster = status.moviePoster
                    status.listPosition?.let {
                        val movie: MovieObject?
                        if(status.isFavorite == true) {
                            movie = favoriteMovies[status.listPosition]
                        } else {
                            movie =  movieList[status.listPosition]
                        }
                        movie?.moviePoster = poster
                        movieListAdapter.notifyItemChanged(status.listPosition)
                    } // If null, the user will just see the default image
                }
                VaroMovieViewModel.Status.FAVORITES_LIST -> {
                    favoriteMovies = status.favorites?.movieList?.toMutableList() ?: mutableListOf()
                    for(movie in favoriteMovies) {
                        movie?.posterPath?.let {
                            viewModel.getMoviePoster(it, favoriteMovies.indexOf(movie), true)
                        } // we don't know the poster path (or there was an error) so don't bother trying to retrieve it
                    }
                    viewModel.getMoviesByPage(currentPage)
                }
                VaroMovieViewModel.Status.FAVORITES_ERROR -> {
                    viewModel.getMoviesByPage(currentPage)
                }
                VaroMovieViewModel.Status.CACHED_MOVIES -> {
                    val movies = status.cachedMovies?.movieList
                    for(mov in movies ?: mutableListOf()) {
                        if(favoriteMovies.filter { it?.id == mov?.id }.isNotEmpty() ) {
                            mov?.isFavorite = true
                        }
                        movieList.add(mov)
                    }

                    for(movie in movieList) {
                        movie?.posterPath?.let {
                            viewModel.getMoviePoster(it, movieList.indexOf(movie), false)
                        } // we don't know the poster path (or there was an error) so don't bother trying to retrieve it
                    }
                    loading = false
                }
                VaroMovieViewModel.Status.CACHED_ERROR -> {
                    viewModel.getMovies(status.pageId!!)
                }
            }
        })
    }

    fun addToFavorites(movie: MovieObject) {
        favoriteMovies.add(movie)
        movieListAdapter.notifyDataSetChanged()
        viewModel.saveFavoritesToDb(favoriteMovies.toList())
    }

    fun removeFromFavorites(movie: MovieObject, position: Int) {
        val list = movieList.filter { it?.id == movie.id }
        if(list.isNotEmpty()) {
            list[0]?.isFavorite = false
        }
        if(showingFavorites) {
            favoriteMovies.removeAt(position)
            movieListAdapter.notifyItemRemoved(position)
        } else {
            favoriteMovies = favoriteMovies.filterNot { it?.id == movie.id }.toMutableList()
        }
        viewModel.saveFavoritesToDb(favoriteMovies.toList())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)

        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView

        val onQuery = object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus() //dismiss keyboard. Kind of hacky
                if(!query.isNullOrEmpty()) {
                    searching = true
                    searchResults = movieList.filter { it?.title?.contains(query!!, true) == true }.toMutableList()
                    movieListAdapter = MovieListAdapter(searchResults, this@VaroMovieActivity)
                    movieListRecyclerView.adapter = movieListAdapter
                } else {
                    searching = false
                    movieListAdapter = MovieListAdapter(movieList, this@VaroMovieActivity)
                    movieListRecyclerView.adapter = movieListAdapter
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrEmpty()) {
                    searching = false
                    movieListAdapter = MovieListAdapter(movieList, this@VaroMovieActivity)
                    movieListRecyclerView.adapter = movieListAdapter
                    searchView.clearFocus()
                }
                return true
            }

        }

        searchView.setOnQueryTextListener(onQuery)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_favorite -> {
            if(showingFavorites) {
                showingFavorites = false
                movieListAdapter = MovieListAdapter(movieList, this)
            } else {
                showingFavorites = true
                movieListAdapter = MovieListAdapter(favoriteMovies, this)
            }
            movieListRecyclerView.adapter = movieListAdapter
            movieListAdapter.notifyDataSetChanged()
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

}
