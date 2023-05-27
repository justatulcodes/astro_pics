package com.expeknow.nasabrowser

import android.app.Application
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.astropics.DetailsWindow
import com.example.astropics.FavoritesWindow
import com.example.astropics.HomeScreen
import com.example.astropics.SearchWindow
import com.expeknow.nasabrowser.BottomNav.BottomMenu
import com.expeknow.nasabrowser.BottomNav.BottomNavMenu
import com.expeknow.nasabrowser.models.APODModel
import com.expeknow.nasabrowser.network.NasaManager
import com.expeknow.nasabrowser.room.MainViewModel
import com.expeknow.nasabrowser.room.MainViewModelFactory
import com.expeknow.nasabrowser.ui.windows.ImageWindow
import java.net.URLDecoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NasaApp(navController: NavHostController, scrollState: ScrollState) {
    val nasaManager: NasaManager = remember {NasaManager()}
    val context = LocalContext.current
    val mMainViewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(context.applicationContext as Application)
    )
    val allSavedImages = mMainViewModel.allImages.observeAsState(listOf()).value


    Scaffold(bottomBar = {
        if(currentRoute(navController = navController) != "imagesWindow/{imageUrl}/{title}"){
            BottomMenu(navController = navController)
        }
        }) {
        NavHost(navController = navController, startDestination = BottomNavMenu.Home.route,
        modifier = Modifier.padding(it)){


            composable(BottomNavMenu.Favorite.route){
                FavoritesWindow(navController, mainViewModel = mMainViewModel)
            }
            composable(BottomNavMenu.Home.route){
                HomeScreen(navController = navController, nasaManager, mainViewModel = mMainViewModel)
            }
            composable(BottomNavMenu.Search.route) {
                SearchWindow(navController, nasaManager, mainViewModel = mMainViewModel)
            }
            composable("imagesWindow/{imageUrl}/{title}",
            arguments = listOf(navArgument("imageUrl"){type= NavType.StringType},
                                navArgument("title"){type = NavType.StringType}
            )
            ){
                val encodedUrl = it.arguments?.getString("imageUrl")
                val decodedUrl = URLDecoder.decode(encodedUrl, "UTF-8")
                val title = it.arguments?.getString("title")!!
                ImageWindow(navController = navController, imageUrl = decodedUrl, title = title)
            }
            composable("details/{index}/{windowCode}",
            arguments = listOf(navArgument("index"){type = NavType.IntType},
                            navArgument("windowCode"){type = NavType.IntType})
            ){
                val index = it.arguments?.getInt("index")
                val windowCode = it.arguments?.getInt("windowCode")
                if(windowCode == Constants.APOD_DETAILS){
                    DetailsWindow(data = nasaManager.apodSingleResponse.value,
                        navController = navController, mMainViewModel)
                }else if(windowCode == Constants.HOME_WINDOW_DETAILS){
                    DetailsWindow(data = nasaManager.apodMultiResponse.value[index!!],
                        navController = navController, mMainViewModel)
                }else if(windowCode == Constants.SEARCH_WINDOW_DETAILS){
                    val data = nasaManager.nasaImageSearch.value.collection
                    val nasaImageData = APODModel(
                        date = data!!.items[index!!].data[0].date_created.substringBefore("T"),
                        explanation = data.items[index].data[0].description,
                        title = data.items[index].data[0].title,
                        url = data.items[index].links[0].href,
                        media_type = data.items[index].data[0].media_type,
                        hdurl = data.items[index].links[0].href
                    )
                    DetailsWindow(data = nasaImageData,
                        navController = navController, mMainViewModel)
                }else{
                    val nasaImageData = APODModel(
                        date = allSavedImages[index!!].date,
                        explanation = allSavedImages[index].description,
                        title = allSavedImages[index].title,
                        url = allSavedImages[index].thumbUrl,
                        media_type = "",
                        hdurl = allSavedImages[index].thumbUrl
                    )
                    DetailsWindow(data = nasaImageData,
                        navController = navController, mMainViewModel)
                }

            }
        }
    }

}
@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

