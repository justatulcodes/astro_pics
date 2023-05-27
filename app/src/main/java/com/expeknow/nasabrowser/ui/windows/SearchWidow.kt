package com.example.astropics

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Snackbar
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.expeknow.nasabrowser.Constants
import com.expeknow.nasabrowser.R
import com.expeknow.nasabrowser.network.NasaManager
import com.expeknow.nasabrowser.room.ImageData
import com.expeknow.nasabrowser.room.MainViewModel
import com.skydoves.landscapist.coil.CoilImage
import dagger.hilt.android.internal.managers.ServiceComponentManager

@OptIn(
    ExperimentalMaterialApi::class
)
@Composable
fun SearchWindow(navController: NavController,
    nasaManager: NasaManager, mainViewModel: MainViewModel) {

    var background by remember {
        mutableStateOf(R.drawable.background3)
    }
    val searchedData = nasaManager.nasaImageSearch.value

    Box(modifier = Modifier.fillMaxSize()){
        Image(painter = painterResource(id = background),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize())

        Column(Modifier.fillMaxSize()) {
            Card(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(8.dp, 20.dp),
                elevation = 6.dp,
                shape = RoundedCornerShape(30.dp)
            ) {
                SearchBar(nasaManager)
            }

            if(isInternetAvailable()){
                LazyColumn(
                ) {

                    if(searchedData.collection?.items == null){
                        item {

                            Text(text="Search",
                                modifier = Modifier.padding(start = 20.dp, top = 5.dp),
                                fontWeight = FontWeight.Black,
                                fontSize = 35.sp,
                                color = Color.White,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }else{
                        item {
                            Text(text="${searchedData.collection.metadata.total_hits} Results...",
                                modifier = Modifier.padding(start = 20.dp, top = 0.dp, bottom = 10.dp),
                                fontWeight = FontWeight.Black,
                                fontSize = 35.sp,
                                color = Color.White,
                                fontFamily = FontFamily.Serif
                            )
                        }

                        val data = searchedData.collection.items
                        items(data.size) { index ->
                            var isFavoriteAPOD by rememberSaveable {
                                mutableStateOf(false)
                            }
                            Box(modifier = Modifier
                                .padding(top = 6.dp, start = 8.dp, end = 8.dp, bottom = 6.dp)
                                .height(200.dp)
                                .fillMaxWidth()) {

                                Card(elevation = 8.dp, modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                    backgroundColor = Color.Black,
                                    onClick = {navController.navigate("details/$index/${Constants.SEARCH_WINDOW_DETAILS}")}
                                )
                                {
                                    CoilImage(imageModel = data[index].links[0].href,
                                        contentScale = ContentScale.Crop,
                                        error = ImageBitmap.imageResource(id = R.drawable.error),
                                        placeHolder = ImageBitmap.imageResource(id = R.drawable.loading)
                                    )
                                }

                                Row(modifier = Modifier.padding(start = 10.dp, top = 170.dp)) {
                                    Text(text = data[index].data[0].title,
                                        color = Color.White, fontSize = 18.sp,
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
                                            data[index].let {
                                                mainViewModel.addImage(
                                                    imageData = ImageData(
                                                        title= data[index].data[0].title?:"No title",
                                                        description = data[index].data[0].description?:"No Description available",
                                                        hdUrl = data[index].links[0].href?:"",
                                                        thumbUrl = data[index].links[0].href?:"",
                                                        date = data[index].data[0].date_created ?: "Not available",
                                                        id = 0
                                                    )
                                                )
                                            }
                                        }
                                        else{
                                            data[index].let{
                                                mainViewModel.deleteImageByUrl(data[index].links[0].href?: "")
                                            }
                                        }
                                    }) {
                                        androidx.compose.material3.Icon(
                                            painter = painterResource(
                                                id = if(isFavoriteAPOD) R.drawable.ic_heart_filled else R.drawable.ic_heart_empty),
                                            contentDescription = "",
                                            tint = if(isFavoriteAPOD) Color.Red else Color.White
                                        )
                                    }
                                }

                            }
                        }
                    }

                }
            }else{
                Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text="No Internet Available",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = Color.White,
                        fontFamily = FontFamily.Serif,
                    )
                }
            }

        }
    }



}

@Composable
fun isInternetAvailable() : Boolean {
    val connectivityManager = LocalContext.current.getSystemService(Context.CONNECTIVITY_SERVICE)
            as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities != null &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun SearchBar(nasaManager: NasaManager) {

    var inputText by remember {
        mutableStateOf(TextFieldValue(""))
    }

    TextField(
        value = inputText,
        onValueChange = { value ->
            inputText = value
        },
        modifier = Modifier.fillMaxWidth(),
        textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
        leadingIcon = {
            IconButton(
                onClick = {
                    nasaManager.getSearchedImages(q = inputText.text)
                }
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(start = 15.dp, end = 15.dp)
                        .size(24.dp)
                )
            }

        },
        trailingIcon = {
            if (inputText != TextFieldValue("")) {
                IconButton(
                    onClick = {
                        inputText =
                            TextFieldValue("") // Remove text from TextField when you press the 'X' icon
                    }
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "",
                        modifier = Modifier
                            .padding(start = 15.dp, end = 15.dp)
                            .size(24.dp)
                    )
                }
            }
        },
        singleLine = true,
        shape = RectangleShape, // The TextFiled has rounded corners top left and right by default
        colors = TextFieldDefaults.textFieldColors(
            textColor = Color.Black,
            cursorColor = Color.Black,
            leadingIconColor = Color.Black,
            trailingIconColor = Color.Black,
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        keyboardActions = KeyboardActions(
            onDone = { nasaManager.getSearchedImages(q = inputText.text)})
    )
}


@Composable
fun LoadingView(onDismiss:() -> Unit) {
    Dialog(onDismissRequest = { onDismiss() }) {

        Card(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier,
            elevation = 8.dp
        ) {
            Column(
                Modifier
                    .background(Color.White)
                    .padding(12.dp)
            ) {
                androidx.compose.material.Text(
                    text = "Loading.. Please wait..",
                    Modifier
                        .padding(8.dp), textAlign = TextAlign.Center
                )

                CircularProgressIndicator(
                    strokeWidth = 4.dp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun search(nasaManager: NasaManager, q : String) {
    if(isInternetAvailable()){
        nasaManager.getSearchedImages(q)
    }else{
        Snackbar() {
            Text(text = "No Internet Connection Available")
        }
    }
}
