package com.varomovie.varomovie.models

import com.google.gson.annotations.SerializedName

data class MovieResponseObject (
    @SerializedName("page") var page: Int?,
    @SerializedName("results") var results: List<MovieObject?>? = mutableListOf(),
    @SerializedName("dates") var dates: MovieDateObject?,
    @SerializedName("total_pages") var totalPages: Int?,
    @SerializedName("total_results") var totalResults: Int?
)
