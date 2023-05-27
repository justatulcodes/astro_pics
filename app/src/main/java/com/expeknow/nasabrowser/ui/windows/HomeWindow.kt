package com.example.astropics


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.expeknow.nasabrowser.Constants
import com.expeknow.nasabrowser.models.APODModel
import com.expeknow.nasabrowser.network.NasaManager
import com.expeknow.nasabrowser.room.ImageData
import com.expeknow.nasabrowser.room.MainViewModel
import com.expeknow.nasabrowser.R
import com.skydoves.landscapist.coil.CoilImage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavController, nasaManager: NasaManager,
    mainViewModel: MainViewModel) {
    var todayApod = APODModel()
    var randomApod = listOf<APODModel>()

    var isFavorite by rememberSaveable {
        mutableStateOf(false)
    }

    val isInternetAvailable = isInternetAvailable()
    if(isInternetAvailable){
        if(!nasaManager.hasAPOD)
            nasaManager.getTodayAPOD()
        if(!nasaManager.hasRandom)
            nasaManager.getRandomAPOD(15)
        todayApod = nasaManager.apodSingleResponse.value
        randomApod = nasaManager.apodMultiResponse.value
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.background3),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize())

        LazyColumn(
        ) {
            item {

                Text(text="Astronomy Picture for",
                    modifier = Modifier.padding(start = 20.dp, top = 30.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                    style = TextStyle(
                        shadow = Shadow(
                            color = Color.DarkGray,
                            offset = Offset(1.0f, 1.0f),
                            blurRadius = 1f
                        )
                    )
                )
                Text(text= SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH).format(Date()),
                    modifier = Modifier.padding(start = 20.dp),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 40.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color.White,
                )
                Box(modifier = Modifier
                    .padding(15.dp)
                    .height(250.dp)
                    .fillMaxWidth()) {

                    Card(elevation = 8.dp,
                        shape = RoundedCornerShape(15.dp),
                        backgroundColor = Color.Black,
                        onClick = {
                        if(isInternetAvailable){
                            navController.navigate(
                                "details/${0}/${Constants.APOD_DETAILS}")
                        }
                        })
                    {
                        if(todayApod.url != null){
                            CoilImage(imageModel = todayApod.url,
                                contentScale = ContentScale.Crop,
                                error = ImageBitmap.imageResource(id = R.drawable.error),
                                placeHolder = ImageBitmap.imageResource(id = R.drawable.loading),
                                modifier = Modifier.fillMaxSize()
                            )
                        }else if(!isInternetAvailable){
                            CoilImage(imageModel = R.drawable.no_internet,
                                contentScale = ContentScale.Crop,
                                error = ImageBitmap.imageResource(id = R.drawable.error),
                                placeHolder = ImageBitmap.imageResource(id = R.drawable.loading),
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                    }

                    Row(modifier = Modifier.padding(start = 10.dp, top = 220.dp, end = 10.dp)) {
                        Text(text = todayApod.title ?: "",
                            modifier = Modifier.weight(2f),
                            color = Color.White, fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                shadow = Shadow(
                                    color = Color.DarkGray,
                                    offset = Offset(1.0f, 1.0f),
                                    blurRadius = 1f
                                )
                            )
                        )
                        if(todayApod.url != null){
                            IconButton(onClick = {
                                isFavorite = !isFavorite
                                if(isFavorite){
                                    todayApod.let {
                                        mainViewModel.addImage(
                                            imageData = ImageData(
                                                title= todayApod.title?:"No title",
                                                description = todayApod.explanation?:"No Description available",
                                                hdUrl = todayApod.hdurl?:"",
                                                thumbUrl = todayApod.url?:"",
                                                date = todayApod.date ?: "Not available",
                                                id = 0
                                            )
                                        )
                                    }
                                }
                                else{
                                    todayApod.let{
                                        mainViewModel.deleteImageByUrl(todayApod.url?: "")
                                    }
                                }
                            }) {
                                Icon(
                                    painter = painterResource(
                                        id = if(isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_empty),
                                    contentDescription = "",
                                    tint = if(isFavorite) Color.Red else Color.White
                                )
                            }
                        }

                    }

                }

                if(isInternetAvailable){
                    Text(text= if(randomApod.isEmpty()) "Loading Images" else "Random Picks",
                        modifier = Modifier.padding(start = 20.dp, bottom = 20.dp, top = 10.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 35.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color.White,
                    )
                }
            }
            if(isInternetAvailable) {
                items(randomApod.size) {
                        index ->
                    var isFavoriteAPOD by rememberSaveable {
                        mutableStateOf(false)
                    }
//                    if(index == randomApod.size-2){
//                        nasaManager.getRandomAPOD(10)
//                    }

                    Box(modifier = Modifier
                        .padding(top = 4.dp, start = 16.dp, end = 16.dp, bottom = 4.dp)
                        .height(200.dp)
                        .fillMaxWidth()) {

                        Card(elevation = 8.dp, modifier = Modifier
                            .fillMaxWidth(),
                            backgroundColor = Color.Black,
                            onClick = {navController.navigate("details/$index/${Constants.HOME_WINDOW_DETAILS}")}
                        )
                        {
                            CoilImage(imageModel = randomApod[index].url,
                                contentScale = ContentScale.Crop,
                                error = ImageBitmap.imageResource(id = R.drawable.error),
                                placeHolder = ImageBitmap.imageResource(id = R.drawable.loading),
                                modifier = Modifier.fillMaxSize())
                        }

                        Row(modifier = Modifier.padding(start = 10.dp, top = 170.dp)) {
                            Text(text = randomApod[index].title!!,
                                color = Color.White, fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(2f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    shadow = Shadow(
                                        color = Color.DarkGray,
                                        offset = Offset(1.0f, 1.0f),
                                        blurRadius = 1f
                                    )
                                )
                            )
                            IconButton(onClick = {
                                isFavoriteAPOD = !isFavoriteAPOD
                                if(isFavoriteAPOD){
                                    randomApod[index].let {
                                        mainViewModel.addImage(
                                            imageData = ImageData(
                                                title= randomApod[index].title?:"No title",
                                                description = randomApod[index].explanation?:"No Description available",
                                                hdUrl = randomApod[index].hdurl?:"",
                                                thumbUrl = randomApod[index].url?:"",
                                                date = randomApod[index].date ?: "Not available",
                                                id = 0
                                            )
                                        )
                                    }
                                }
                                else{
                                    randomApod[index].let{
                                        mainViewModel.deleteImageByUrl(randomApod[index].url?: "")
                                    }
                                }

                            }) {
                                Icon(painter = painterResource(
                                    id = if(isFavoriteAPOD) R.drawable.ic_heart_filled else R.drawable.ic_heart_empty),
                                    contentDescription = "",
                                    tint = if(isFavoriteAPOD) Color.Red else Color.White
                                )
                            }
                        }

                    }

                }

                if(randomApod.isNotEmpty()){
                    item {
                        Button(
                            onClick = { nasaManager.getRandomAPOD(10) },
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.black),
                                contentColor = Color.White
                            ),
                        ) {
                            Text(text = "Load More...", color = Color.White)
                        }
                    }
                }

            }

        }
    }


}
