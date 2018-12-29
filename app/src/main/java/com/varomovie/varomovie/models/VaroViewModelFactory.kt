package com.varomovie.varomovie.models

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.persistence.room.Room
import android.support.v7.app.AppCompatActivity
import com.varomovie.varomovie.VaroMovieRepository
import com.varomovie.varomovie.VaroMovieViewModel
import com.varomovie.varomovie.dagger.ApiModule
import com.varomovie.varomovie.db.VaroMovieDatabase
import java.lang.IllegalArgumentException

class VaroViewModelFactory(private val activity: AppCompatActivity) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(VaroMovieViewModel::class.java)) {
            val api = ApiModule()
            val provider = Room.databaseBuilder(activity.applicationContext, VaroMovieDatabase::class.java, "varoDB").build()
            val repository = VaroMovieRepository(api.provideNetService(), provider.varoMovieDao())
            @Suppress("UNCHECKED_CAST")
            return VaroMovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModelClass")
    }
}