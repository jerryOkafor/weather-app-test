package com.jerryokafor.weatherapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.jerryokafor.weatherapp.OnDestinationChanged
import com.jerryokafor.weatherapp.data.*
import com.jerryokafor.weatherapp.fromMetric
import com.jerryokafor.weatherapp.model.City
import com.jerryokafor.weatherapp.model.Weather
import com.jerryokafor.weatherapp.ui.theme.AppBackground
import com.jerryokafor.weatherapp.ui.theme.ColorAccent
import com.jerryokafor.weatherapp.ui.theme.ColorSecondary
import com.jerryokafor.weatherapp.ui.theme.WHITE_50
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import okhttp3.internal.wait
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*
import javax.inject.Inject

/**
 * @Author Jerry Okafor
 * @Project WeatherApp
 * @Date 11/10/2021 09:44
 */

@HiltViewModel
class WeatherDetailsViewModel @Inject constructor() : ViewModel() {

    private val _city = MutableStateFlow<Resource<City>>(Resource.Loading)
    val city: StateFlow<Resource<City>>
        get() = _city


    fun setCityId(id: String) {
        CityRepository.getCity(id.toLong())?.let {
            _city.value = Resource.Success(it)
        }

    }
}

@Composable
fun WeatherDetails(
    viewModel: WeatherDetailsViewModel = hiltViewModel(),
    cityId: String,
    onDestinationChanged: OnDestinationChanged,
) {

    val city by viewModel.city.collectAsState()

    LaunchedEffect(cityId) {
        viewModel.setCityId(cityId)
    }

    Box {
        with(city) {
            loading {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center),
                    color = ColorSecondary,
                    strokeWidth = Dp(value = 2F)
                )
            }
            onSuccess {
                onDestinationChanged(it.name)
                Timber.d("City: $it")
                WeatherDetailsContent(it)
            }
            onFailure {
                Timber.w("Error: $it")
            }
        }
    }


}

@ExperimentalCoilApi
@Composable
fun WeatherDetailsContent(city: City) {
    val currentWeather = remember { mutableStateOf(city.weather?.first()!!) }

    val date = Date(city.date * 1000)
    Timber.d("Date: $date")
    val dateFormatter =
        SimpleDateFormat("E, dd MMM yyyy HH:mm", Locale.getDefault()) //this format changeable

    Column(
        Modifier
            .background(color = AppBackground)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))
        Surface(
            color = WHITE_50,
            shape = RoundedCornerShape(18.dp),
        ) {
            Text(
                text = dateFormatter.format(date), style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(8.dp),
                color = Color.White
            )
        }
        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Surface(
                color = WHITE_50,
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(0.4f),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "${city.main?.tempMax?.fromMetric()} °",
                            style = MaterialTheme.typography.h2,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "${city.main?.tempMin?.fromMetric()} °",
                            style = MaterialTheme.typography.h4,
                            color = Color.White
                        )

                    }
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = currentWeather.value.description.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            },
                            style = MaterialTheme.typography.h6,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Box {
                            val painter = rememberImagePainter(
                                data = "https://openweathermap.org/img/wn/${currentWeather.value.icon}@2x.png",
                                onExecute = { _, _ -> true },
                                builder = {
                                    crossfade(true)
//                        placeholder(R.drawable.placeholder)
                                    transformations(CircleCropTransformation())
                                }
                            )
                            Image(
                                painter = painter,
                                contentDescription = null,
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(100.dp)
                            )

                            when (painter.state) {
                                is ImagePainter.State.Loading -> CircularProgressIndicator(
                                    Modifier.align(Alignment.Center),
                                    color = Color.LightGray,
                                    strokeWidth = 1.5.dp
                                )
                                is ImagePainter.State.Error -> {
                                }
                                is ImagePainter.State.Success -> {
                                }
                                is ImagePainter.State.Empty -> {
                                }
                            }
                        }
                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Surface(
            color = WHITE_50,
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                LazyRow() {
                    val weathers = city.weather ?: emptyList()
                    items(weathers) { city ->
                        WeatherItem(item = city) {}
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Humidity:",
                        style = MaterialTheme.typography.h6,
                        color = ColorAccent
                    )
                    Text(
                        text = "${city.main?.humidity} %",
                        style = MaterialTheme.typography.h6,
                        color = ColorAccent
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Pressure:",
                        style = MaterialTheme.typography.h6,
                        color = ColorAccent
                    )
                    Text(
                        text = "${city.main?.pressure} hPa",
                        style = MaterialTheme.typography.h6,
                        color = ColorAccent
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Wind Speed:",
                        style = MaterialTheme.typography.h6,
                        color = ColorAccent
                    )
                    Text(
                        text = "${city.wind?.speed} Km/h S",
                        style = MaterialTheme.typography.h6,
                        color = ColorAccent
                    )
                }
            }
        }

    }
}

@ExperimentalCoilApi
@Preview("WeatherDetails")
@Composable
fun WeatherDetailsPreview() {
    WeatherDetailsContent(City())
}

@ExperimentalCoilApi
@Composable
fun WeatherItem(
    modifier: Modifier = Modifier,
    item: Weather,
    onItemClicked: OnCityItemClicked
) {
    Column(
        modifier = modifier
            .clickable { }
            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageContainer {
            Box {
                val painter = rememberImagePainter(
                    data = "https://openweathermap.org/img/wn/${item.icon}@2x.png",
                    onExecute = { _, _ -> true },
                    builder = {
                        crossfade(true)
//                        placeholder(R.drawable.placeholder)
                        transformations(CircleCropTransformation())
                    }
                )
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier.size(128.dp)
                )

                when (painter.state) {
                    is ImagePainter.State.Loading -> CircularProgressIndicator(
                        Modifier.align(Alignment.Center),
                        color = Color.LightGray,
                        strokeWidth = 1.5.dp
                    )
                    is ImagePainter.State.Error -> {
                    }
                    is ImagePainter.State.Success -> {
                    }
                    is ImagePainter.State.Empty -> {
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = item.main,
                    style = MaterialTheme.typography.subtitle1
                )
            }

        }
    }
}


@Preview(name = "CityWeather")
@Composable
fun WeatherItemPreview() {
    WeatherItem(item = Weather()) {
    }
}