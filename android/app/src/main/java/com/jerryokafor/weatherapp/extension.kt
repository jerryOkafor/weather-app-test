package com.jerryokafor.weatherapp

/**
 * @Author Jerry Okafor
 * @Project WeatherApp
 * @Date 11/10/2021 15:53
 */

fun Double.toCelsius(): Double = ((this - 32) * 5) / 9
fun Double.fromMetric(): Int = (this - 273.15).toInt()