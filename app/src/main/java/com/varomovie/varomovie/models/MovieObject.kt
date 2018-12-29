package com.varomovie.varomovie.models

import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MovieObject (
    @PrimaryKey
    @SerializedName("id") var id: Int?,
    @SerializedName("poster_path") var posterPath: String? = null,
    @SerializedName("adult") var adult: Boolean? = false,
    @SerializedName("overview") var overview: String? = "",
    @SerializedName("release_date") var releaseDate: String? = "",
    @SerializedName("genre_ids") var genreIds: List<Int>? = listOf(),
    @SerializedName("original_title") var originalTitle: String? = "",
    @SerializedName("original_language") var originalLanguage: String? = "",
    @SerializedName("title") var title: String? = "",
    @SerializedName("backdrop_path") var backdropPath: String? = null,
    @SerializedName("popularity") var popularity: Double?,
    @SerializedName("vote_count") var voteCount: Int? = 0,
    @SerializedName("video") var video: Boolean? = false,
    @SerializedName("vote_average") var voteAverage: Double?,
    var isFavorite: Boolean? = false,
    @Ignore
    @Transient
    @IgnoredOnParcel
    var moviePoster: Bitmap? = null
) : Parcelable {
    companion object {
        const val MOVIE_OBJECT_INTENT = "MOVIE_OBJECT_INTENT"
    }
}
