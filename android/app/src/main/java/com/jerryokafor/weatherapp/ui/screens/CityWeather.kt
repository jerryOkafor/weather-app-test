package com.jerryokafor.weatherapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.jerryokafor.weatherapp.OnDestinationChanged
import com.jerryokafor.weatherapp.api.WeatherService
import com.jerryokafor.weatherapp.cities
import com.jerryokafor.weatherapp.data.*
import com.jerryokafor.weatherapp.fromMetric
import com.jerryokafor.weatherapp.model.City
import com.jerryokafor.weatherapp.ui.theme.AppBackground
import com.jerryokafor.weatherapp.ui.theme.ColorSecondary
import com.jerryokafor.weatherapp.ui.theme.WHITE_80
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * @Author Jerry Okafor
 * @Project WeatherApp
 * @Date 11/10/2021 10:52
 */


typealias OnCityItemClicked = (City) -> Unit

@HiltViewModel
class CitiesViewModel @Inject constructor(private val weatherService: WeatherService) : ViewModel() {

    private val _cityWeather = MutableStateFlow<Resource<List<City>>>(Resource.Loading)
    val cityWeather: StateFlow<Resource<List<City>>>
        get() = _cityWeather

    init {
        if (CityRepository.isEmpty()) {
            loadWeatherData()
        }
    }

    private fun loadWeatherData() {
        _cityWeather.value = Resource.Loading

        viewModelScope.launch {
            val requests = cities.toList().map {
                async {
                    try {
                        val city = weatherService.getCity(it)
                        Timber.d("City: $city")
                        CityRepository.addCity(city = city)
                    } catch (e: Exception) {
                        Timber.w(e)
                    }
                }
            }
            requests.awaitAll()

            val allCities = CityRepository.cities.map { it.value }
            _cityWeather.value = Resource.Success(allCities)
        }
    }

}

@ExperimentalCoilApi
@Composable
fun CityWeather(
    viewModel: CitiesViewModel = hiltViewModel(),
    navHostController: NavHostController,
    onDestinationChanged: OnDestinationChanged
) {
    val allCities by viewModel.cityWeather.collectAsState()

    LaunchedEffect(onDestinationChanged) {
        onDestinationChanged("Weather App")
    }

    Surface(color = AppBackground) {
        Box(Modifier.fillMaxSize()) {
            with(allCities) {
                loading {
                    Timber.d("Loading")
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.Center),
                        color = ColorSecondary,
                        strokeWidth = Dp(value = 2F)
                    )
                }
                onSuccess {
                    Timber.d("Cities: ${it.size}")
                    LazyColumn {
                        items(it) { city ->
                            CityItem(item = city) {
                                navHostController.navigate("weatherDetails/${city.id}")
                            }
                        }
                    }
                }
                onFailure {
                    Timber.d("Error")
                    Column {
//                        Image(
//                            painter = painterResource(id = R),
//                            contentDescription = null,
//                            modifier = Modifier.size(128.dp)
//                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(text = "Error loading weather: $it")
                    }
                }

            }
        }
    }
}


@Composable
fun ImageContainer(content: @Composable () -> Unit) {
    Surface(
        color = WHITE_80,
        modifier = Modifier.size(width = 60.dp, height = 60.dp),
        shape = RoundedCornerShape(4.dp)
    ) {
        content()
    }
}

@ExperimentalCoilApi
@Composable
fun CityItem(
    modifier: Modifier = Modifier,
    item: City,
    onItemClicked: OnCityItemClicked
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClicked(item) }
            .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp)
    ) {
        ImageContainer {
            Box {
                val painter = rememberImagePainter(
                    data = "https://openweathermap.org/img/wn/${item.weather?.firstOrNull()?.icon}@2x.png",
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
        Spacer(Modifier.width(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.h6
                )
                Spacer(Modifier.height(10.dp))
                val style1 = SpanStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = MaterialTheme.colors.onBackground.copy(0.6f)
                )
                val style2 = SpanStyle(
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = MaterialTheme.colors.onBackground.copy(0.4f)
                )

                val annotatedString = buildAnnotatedString {
                    withStyle(style = style1) {
                        append("Wind")
                    }
                    withStyle(style = style2) {
                        append(" ${item.wind?.speed} m/h")
                    }

                    withStyle(style = style1) {
                        append("  Visibility")
                    }
                    withStyle(style = style2) {
                        append(" ${item.visibility / 1000} km")
                    }
                }
                Text(annotatedString)
            }
            Text(
                text = "${item.main?.temp?.fromMetric()?.toInt()} °",
                style = MaterialTheme.typography.h4,
                color = Color(0x73000000)
            )
        }
    }
}


@Preview(name = "CityWeather")
@Composable
fun CityItemPreview() {
    CityItem(item = City()) {

    }
}