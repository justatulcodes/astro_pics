package com.example.astropics

import android.app.DownloadManager
import android.app.WallpaperManager
import android.content.BroadcastReceiver
import coil.*
import android.content.Context
import android.content.Context.*
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.provider.DocumentsContract
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import com.expeknow.nasabrowser.R
import com.expeknow.nasabrowser.models.APODModel
import com.expeknow.nasabrowser.network.NasaManager
import com.expeknow.nasabrowser.room.ImageData
import com.expeknow.nasabrowser.room.MainViewModel
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.net.URLEncoder
import java.time.Duration

var downloadId : Long? = null

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DetailsWindow(data: APODModel, navController: NavController, mMainViewController: MainViewModel) {


    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    var isFavorite by rememberSaveable {
        mutableStateOf(false)
    }
    var isShared : Boolean = false

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { TopBar(navController)}
    ) {
        
        Box(modifier = Modifier.fillMaxSize()) {

            Image(painter = painterResource(id = R.drawable.background3),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize())
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(it)
                    .verticalScroll(rememberScrollState())
            ) {

                SelectionContainer {
                    Text(
                        text = data.title ?: "Not Available",
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        fontSize = 25.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color.White
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Card(elevation = 8.dp, modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        onClick = {
                            val encodedUrl = URLEncoder.encode(data.hdurl ?: data.url, "UTF-8")
                            navController.navigate("imagesWindow/$encodedUrl/${data.title}")
                        }
                    ) {


                        CoilImage(imageModel = data.url,
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center,
                            error = ImageBitmap.imageResource(id = R.drawable.error),
                            placeHolder = ImageBitmap.imageResource(id = R.drawable.loading),
                            modifier = Modifier.fillMaxSize())
                    }
                }


                Row(modifier = Modifier.padding(5.dp)) {
                    Button(onClick = { downloadImage(data, context, coroutineScope,
                        scaffoldState) },
                        Modifier
                            .padding(start=15.dp, end=0.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(id = R.color.black),
                        )) {
                        Icon(painter = painterResource(id = R.drawable.ic_download),
                            contentDescription = "download", tint = Color.White)
                    }

//                    Button(
//                        onClick = { /*TODO*/ },
//                        Modifier
//                            .padding(start = 10.dp, end = 0.dp),
//                        shape = RoundedCornerShape(50.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            backgroundColor = colorResource(id = R.color.black),
//                        ),
//
//                        ) {
//                        Icon(painter = painterResource(id = R.drawable.ic_set_wallpaer),
//                            contentDescription = "set as wallpaper", tint = Color.White)
//                    }

                    Button(
                        onClick = {
                            isFavorite = !isFavorite
                            if(isFavorite){
                                data.let {
                                    mMainViewController.addImage(
                                        imageData = ImageData(
                                            title= data.title?:"No title",
                                            description = data.explanation?:"No Description available",
                                            hdUrl = data.hdurl?:"",
                                            thumbUrl = data.url?:"",
                                            date = data.date ?: "Not available",
                                            id = 0
                                        )
                                    )
                                }
                            }
                            else{
                                data.let{
                                    mMainViewController.deleteImageByUrl(data.url?: "")
                                }
                            }
                        },
                        Modifier
                            .padding(start = 10.dp, end = 0.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(id = R.color.black),
                        ),

                        ) {
                        Icon(painter = painterResource(
                            id = if(isFavorite) R.drawable.ic_heart_filled else R.drawable.ic_heart_empty),
                            contentDescription = "add as favorite",
                            tint = if(isFavorite) Color.Red else Color.White
                        )
                    }

                    Button(
                        onClick = {
                            shareData(data, context, coroutineScope, scaffoldState)
                        },
                        Modifier
                            .padding(start = 10.dp, end = 0.dp),
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = colorResource(id = R.color.black),
                        ),

                        ) {
                        Icon(painter = painterResource(
                            id = R.drawable.ic_share),
                            contentDescription = "share",
                            tint = Color.White
                        )
                    }
                }


                Text(
                    text = "Published on: ${data.date}",
                    modifier = Modifier
                        .padding(start = 20.dp, top = 5.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.LightGray,
                    fontFamily = FontFamily.Serif
                )


                SelectionContainer {
                    Text(
                        text = data.explanation ?: "Nothing Mentioned",
                        modifier = Modifier
                            .padding(20.dp, 10.dp),
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

            }

        }

    }

}

fun shareData(data: APODModel, context: Context,
              coroutineScope: CoroutineScope, scaffoldState: ScaffoldState) {
    val filePath =
        "/storage/emulated/0/Download/${data.title?.replace(":", "_")}.jpg"
    val file = File(filePath)

    if (file.exists()){
        val uri = Uri.fromFile(file)
        val intent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "${data.title} \n\n${data.explanation}")
            type = "image/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val shareIntent = Intent.createChooser(intent, "Share AstroPic")
        startActivity(context, shareIntent, null)
        file.deleteOnExit()
    }else{
        if(isInternetAvailable(context)){
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Preparing share data...",
                    duration = SnackbarDuration.Short
                )
            }
            shareDownloadHelper(context, data, coroutineScope, scaffoldState)
        }else{
            coroutineScope.launch {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "No internet connection available",
                    duration = SnackbarDuration.Short
                )
            }
        }

    }

}


fun shareDownloadHelper(context: Context, data: APODModel,
                        coroutineScope: CoroutineScope, scaffoldState: ScaffoldState) {
    if(isInternetAvailable(context)){
        val downloadReceiver = object : BroadcastReceiver() {
            override fun onReceive(context2: Context, intent2: Intent) {

                val id = intent2.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id != -1L) {
                if(id == downloadId){
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = "Image downloaded!",
                        duration = SnackbarDuration.Short
                    )
                } }else{
                    shareData(data, context, coroutineScope, scaffoldState)
                }
                }
            }
        }

        // register BroadcastReceiver
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context.registerReceiver(downloadReceiver, filter)

        // create download request
        val request = DownloadManager.Request(Uri.parse(data.url))
        request.setTitle(data.title) // Title for notification
        request.setDescription("AstroPic Download")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
            "${data.title?.replace(":", "_")}.jpg")
        request.setMimeType("image/jpeg")

        // enqueue download request
        val manager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

    }
}

fun downloadImage(data: APODModel, context: Context,
    coroutineScope: CoroutineScope, scaffoldState: ScaffoldState) {
    if(isInternetAvailable(context)){
        // create download request
        val request = DownloadManager.Request(Uri.parse(data.hdurl))
        request.setTitle(data.title) // Title for notification
        request.setDescription("AstroPic Download")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
            "${data.title?.replace(":", "_")}.jpg")
        request.setMimeType("image/jpeg")
        // enqueue download request
        val manager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadId = manager.enqueue(request)

    }else{
        coroutineScope.launch {
            scaffoldState.snackbarHostState.showSnackbar(
                message = "No internet connection available",
                duration = SnackbarDuration.Short
            )
        }
    }
}


fun isInternetAvailable(context: Context) : Boolean {
    val connectivityManager = context.getSystemService(CONNECTIVITY_SERVICE)
            as ConnectivityManager
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities != null &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}


@Composable
fun TopBar(navController: NavController) {
    TopAppBar(title = { Text(text = "Details", fontWeight = FontWeight.SemiBold, color = Color.White)},
        backgroundColor = Color.Black,
        navigationIcon = { IconButton(onClick = { navController.popBackStack() }) {
            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back", tint = Color.White)
        }})
}

