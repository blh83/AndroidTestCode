package com.varomovie.varomovie.models

import com.google.gson.annotations.SerializedName

data class MovieDateObject (
    @SerializedName("maximum") var maximum: String?,
    @SerializedName("minimum") var minimum: String?
)
