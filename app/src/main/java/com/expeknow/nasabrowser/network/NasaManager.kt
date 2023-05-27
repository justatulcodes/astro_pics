package com.expeknow.nasabrowser.network

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.expeknow.nasabrowser.models.APODModel
import com.expeknow.nasabrowser.models.SearchModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class NasaManager {

    private val _apodResponseSingle = mutableStateOf(APODModel())
    private val _responseList = ArrayList<APODModel>()
    var hasAPOD = false
    var hasRandom = false

    @SuppressLint("MutableCollectionMutableState")
    private val _apodResponseMulti = mutableStateOf(ArrayList<APODModel>())
    val apodMultiResponse : State<List<APODModel>>
        @Composable get() = remember {_apodResponseMulti}

    private val _nasaImageSearch = mutableStateOf(SearchModel())

    val apodSingleResponse : State<APODModel>
        @Composable get() = remember {_apodResponseSingle}


    val nasaImageSearch : State<SearchModel>
        @Composable get() = remember {_nasaImageSearch}


    init {
        getTodayAPOD()
        getRandomAPOD(15)
    }

    fun getTodayAPOD() {
        Log.d("running", "entered function")
        val service = API_APOD.apod.getTodayAPOD(API_APOD.API_KEY)

        service.enqueue(object : Callback<APODModel>{
            override fun onResponse(call: Call<APODModel>, response: Response<APODModel>) {
                if(response.isSuccessful){
                    _apodResponseSingle.value = response.body()!!
                    hasAPOD = true
                }else{
                    Log.d("Error", "Error in fetching Today's APOD ${response.body()}")
                }
            }

            override fun onFailure(call: Call<APODModel>, t: Throwable) {
                Log.d("Error", "Error in fetching Today's APOD $")
            }

        })
    }


    fun getRandomAPOD(count : Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("https://api.nasa.gov/planetary/apod?api_key=${API_APOD.API_KEY}&count=$count")
                val connection = withContext(Dispatchers.IO) {
                    url.openConnection()
                } as HttpURLConnection
                connection.requestMethod = "GET"

                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    val responseString = connection.inputStream.bufferedReader().use(BufferedReader::readText)
                    val jsonArray = JSONArray(responseString)
                    val randomAPODList : ArrayList<APODModel> = ArrayList()
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        if (jsonObject.getString("media_type") == "image"){
                            randomAPODList.add(APODModel(
                                title = jsonObject.getString("title"),
                                date = jsonObject.getString("date"),
                                explanation = jsonObject.getString("explanation"),
                                hdurl = jsonObject.optString("hdurl", jsonObject.getString("url")),
                                url = jsonObject.getString("url"),
                                ))
                        }
                    }

                    _responseList.addAll(randomAPODList)
                    _apodResponseMulti.value = _responseList
                    hasRandom = true

                } else {
                    Log.d("Error", "Error in fetching Random APODs --- ${connection.responseCode} -- " +
                            connection.responseMessage
                    )
                }
            } catch (e: Exception) {
                Log.e("Error", "Error in network call", e)
            }
        }
    }


    fun getDatedAPOD(date : String){
        val service = API_APOD.apod.getDatedAPOD(API_APOD.API_KEY, date)
        service.enqueue(object : Callback<APODModel>{
            override fun onResponse(call: Call<APODModel>, response: Response<APODModel>) {
                if(response.isSuccessful){
                    _apodResponseSingle.value = response.body()!!
                }else{
                    Log.d("Error", "Error in fetching Random APODs -- ${response.body()}")
                }
            }
            override fun onFailure(call: Call<APODModel>, t: Throwable) {
                Log.d("Error", "Error in fetching Dated APOD ${t.message}")
            }

        })
    }


    fun getSearchedImages(q: String){
        val service = API_Search.search.getSearchedImage(q = q)
        service.enqueue(object : Callback<SearchModel>{
            override fun onResponse(call: Call<SearchModel>, response: Response<SearchModel>) {
                if(response.isSuccessful){
                    _nasaImageSearch.value = response.body()!!
                }else{
                    Log.d("Error", "Error in Searching Nasa Images -- ${response.body()}")
                }
            }
            override fun onFailure(call: Call<SearchModel>, t: Throwable) {
                Log.d("Error", "Error in Searching Nasa Images ${t.message}")
            }

        })
    }



}