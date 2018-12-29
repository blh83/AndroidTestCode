package com.varomovie.varomovie.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Entity(tableName = MovieDbObject.tableName)
@Parcelize
data class MovieDbObject (
    @PrimaryKey
    var pageId: Int,
    var movieList: List<MovieObject?>
) : Parcelable {
    companion object {
        const val tableName = "movie_db_object"
    }
}
