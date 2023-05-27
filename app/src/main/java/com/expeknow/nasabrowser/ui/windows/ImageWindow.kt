package com.expeknow.nasabrowser.ui.windows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.expeknow.nasabrowser.R
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageWindow(navController: NavController, imageUrl: String, title: String) {
    Scaffold(topBar = {
        TopBar(navController, title)
    }) {

        Column(
            Modifier
                .fillMaxSize()
                .padding(it)
                .background(Color.Black)
                ) {

            Box(
                Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterHorizontally)
            ) {
                val zoomState = rememberZoomState()
                AsyncImage(model = imageUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(zoomState),
                    placeholder = painterResource(id = R.drawable.loading),
                    error = painterResource(id = R.drawable.error)
                )
            }

        }
    }
}

@Composable
fun TopBar(navController: NavController, title: String){
    TopAppBar(
        title = { Text(text = title,
        fontWeight = FontWeight.SemiBold,
        color = Color.White)
                },
        backgroundColor = Color.Black,
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack,
                    contentDescription = "back",
                    tint = Color.White)
            }
        })
}

