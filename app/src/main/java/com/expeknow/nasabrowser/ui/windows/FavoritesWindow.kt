package com.example.astropics

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
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
import com.expeknow.nasabrowser.R
import com.expeknow.nasabrowser.room.MainViewModel
import com.skydoves.landscapist.coil.CoilImage

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavoritesWindow(navController: NavController, mainViewModel: MainViewModel) {

    val allSavedImages = mainViewModel.allImages.observeAsState(listOf()).value

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background3),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Column(Modifier.fillMaxSize()) {

            if (allSavedImages.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = "No Favorites Yet.",
                        modifier = Modifier
                            .padding(start = 20.dp, top = 40.dp, bottom = 20.dp)
                            .height(50.dp)
                            .fillMaxWidth(),
                        fontWeight = FontWeight.Black,
                        fontSize = 30.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color.White,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                ) {

                    item {
                        Column(Modifier.fillMaxWidth()) {
                            Text(
                                text = "Saved",
                                modifier = Modifier
                                    .padding(start = 20.dp, top = 40.dp, bottom = 20.dp)
                                    .height(50.dp)
                                    .fillMaxWidth(),
                                fontWeight = FontWeight.Black,
                                fontSize = 40.sp,
                                fontFamily = FontFamily.Serif,
                                color = Color.White,
                            )
                        }
                    }
                    item {  }

                    items(allSavedImages.size) { index ->

                            val isFavoriteAPOD by rememberSaveable {
                                mutableStateOf(false)
                            }
                            Box(
                                modifier = Modifier
                                    .padding(top = 4.dp, start = 8.dp, end = 8.dp, bottom = 4.dp)
                                    .height(150.dp)
                                    .fillMaxWidth()
                            ) {

                                Card(elevation = 8.dp, modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                    backgroundColor = Color.Black,
                                    onClick = { navController.navigate("details/$index/${Constants.FAVORITE_WINDOW_DETAILS}") }
                                )
                                {
                                    CoilImage(
                                        imageModel = allSavedImages[index].thumbUrl,
                                        contentScale = ContentScale.Crop,
                                        error = ImageBitmap.imageResource(id = R.drawable.error),
                                        placeHolder = ImageBitmap.imageResource(id = R.drawable.loading)
                                    )
                                }

                                Row(modifier = Modifier.padding(start = 10.dp, top = 120.dp)) {
                                    Text(
                                        text = allSavedImages[index].title,
                                        color = Color.White, fontSize = 16.sp,
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
                                        mainViewModel.deleteImageByUrl(
                                            allSavedImages[index].thumbUrl ?: ""
                                        )
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "",
                                            tint = if (isFavoriteAPOD) Color.White else Color.Red
                                        )
                                    }
                                }

                            }
                        }

                    }

            }

        }
    }

}
