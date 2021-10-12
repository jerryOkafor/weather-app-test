package com.jerryokafor.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.jerryokafor.weatherapp.ui.screens.CityWeather
import com.jerryokafor.weatherapp.ui.screens.WeatherDetails
import com.jerryokafor.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

val cities = setOf(
    "Kenya",
    "Cairo",
    "Lagos",
    "Abuja",
    "New York",
    "Texas",
    "Amazon",
    "Belarus",
    "Lesotho",
    "Jakarta",
    "Ankara",
    "Kano",
    "Peru",
    "Winnipeg",
    "Bagdad",
    "Westham"
)

typealias OnDestinationChanged = (String) -> Unit

@ExperimentalCoilApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        setContent {
            ProvideWindowInsets {
                WeatherAppTheme {
                    // A surface container using the 'background' color from the theme
                    val navHostController = rememberNavController()
                    val navBackStackEntry by navHostController.currentBackStackEntryAsState()

                    val appBarTitle = remember { mutableStateOf("Weather App") }
                    val onDestinationChanged: OnDestinationChanged = { title ->
                        Timber.d("Title: $title")
                        appBarTitle.value = title
                    }
                    Scaffold(
                        modifier = Modifier.navigationBarsWithImePadding(),
                        topBar = {
                            val currentDestination = navBackStackEntry?.destination?.route
                            val navigationIcon: @Composable (() -> Unit)? =
                                if (currentDestination !== "weather") {
                                    {
                                        IconButton(onClick = { navHostController.navigateUp() }) {
                                            Icon(
                                                Icons.Filled.Close,
                                                "Close Icon"
                                            )
                                        }
                                    }
                                } else null
                            TopAppBar(
                                title = { Text(text = appBarTitle.value) },
                                navigationIcon = navigationIcon
                            )
                        },
                    ) {
                        NavHost(navController = navHostController, startDestination = "weather") {
                            composable("weather") {
                                CityWeather(
                                    navHostController = navHostController,
                                    onDestinationChanged = onDestinationChanged
                                )
                            }
                            composable("weatherDetails/{cityId}") { backStackEntry ->
                                val cityId = backStackEntry.arguments?.getString("cityId")!!
                                WeatherDetails(
                                    onDestinationChanged = onDestinationChanged,
                                    cityId = cityId
                                )

                            }
                        }
                    }

                }
            }
        }
    }
}