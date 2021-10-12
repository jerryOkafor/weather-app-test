package com.jerryokafor.weatherapp.data

/**
 * @Author Jerry Okafor
 * @Project WeatherApp
 * @Date 11/10/2021 11:25
 */

sealed class Resource<out T : Any> {
    object Loading : Resource<Nothing>()
    data class Success<out T : Any>(val data: T) : Resource<T>()
    data class Failure(val error: String) : Resource<Nothing>()

}

inline fun <T : Any> Resource<T>.loading(action: () -> Unit): Resource<T> {
    if (this is Resource.Loading) action()
    return this
}


inline fun <T : Any> Resource<T>.onSuccess(action: (T) -> Unit): Resource<T> {
    if (this is Resource.Success) action(data)
    return this
}

inline fun <T : Any> Resource<T>.onFailure(action: (String) -> Unit) {
    if (this is Resource.Failure) action(error)
}