package com.jerryokafor.weatherapp.data

import com.jerryokafor.weatherapp.model.City

/**
 * @Author Jerry Okafor
 * @Project WeatherApp
 * @Date 11/10/2021 11:11
 */
class CityRepository {
    companion object {
        @JvmStatic
        val cities: HashMap<Long, City> = hashMapOf()

        @JvmStatic
        fun addCity(city: City) {
            cities[city.id] = city
        }

        @JvmStatic
        fun getCity(id: Long): City? {
            return cities[id]
        }

        @JvmStatic
        fun isEmpty(): Boolean = cities.isEmpty()

        @JvmStatic
        fun count(): Int = cities.size
    }
}