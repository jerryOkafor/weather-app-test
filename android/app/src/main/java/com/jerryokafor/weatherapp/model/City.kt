package com.jerryokafor.weatherapp.model

import com.google.gson.annotations.SerializedName

data class City(
    val name: String = "",
    val id: Long = 0L,
    @SerializedName("dt")
    val date: Long = 0L,
    val timezone: Int = 0,
    val cod: Int = 0,
    val visibility: Long = 0L,
    val coord: Coord? = null,
    val weather: List<Weather>? = null,
    val base: String? = null,
    val main: MainWeather? = null,
    val wind: Wind? = null,
    val clouds: Clouds? = null,

    )