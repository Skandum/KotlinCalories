package com.example.calories

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_loacation.*
import android.location.Geocoder
import android.util.Log
import android.widget.TextView
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.URL
import java.util.*


class LoacationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMapView: MapView
    private lateinit var mFusedLocationClient : FusedLocationProviderClient

    private var oldProductLocation : LatLng? = null
    private var newProductLocation : LatLng? = null

    private  var resultWeather : TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loacation)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastKnownLocation()

        mMapView = findViewById(R.id.map)
        initGoogleMap(savedInstanceState)

        //возвращаемся назад
        //!!!не удалять
        //при наследовании выглядит некрасиво поэтому через кнопку
        buttonExit.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        resultWeather = findViewById(R.id.textViewWeather)
    }

    private fun getLastKnownLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        mFusedLocationClient.lastLocation.addOnCompleteListener{
            if(oldProductLocation == null && it.isSuccessful){
                val location = it.result
                oldProductLocation = LatLng(location.latitude,location.longitude)
                newProductLocation = oldProductLocation


                //парсим через геокодер название города по долготе и широте
                var cityName: String? = null
                val gcd = Geocoder(baseContext, Locale.getDefault())
                cityName = gcd.getFromLocation(location.latitude,location.longitude,1).get(0).locality

                //ключи котоырй дали на том сайте с погодой
                val key : String = "923b4c854156bdad91149ad6cc457f1d"
                val url : String = "https://api.openweathermap.org/data/2.5/weather?q=$cityName&appid=$key&units=metric&lang=ru"

                //парсим джсон и берем город с температурой
                //можно потом допилить ветер и прочуюю хрень
                doAsync{
                    val apiResponse = URL(url).readText()

                    val weather = JSONObject(apiResponse).getJSONArray("weather")
                    val desc = weather.getJSONObject(0).getString("description")

                    val main = JSONObject(apiResponse).getJSONObject("main")
                    val temp = main.getString("temp")
                    val intTemp = temp.toFloat()

                    val name = JSONObject(apiResponse).getString("name")


                    val l = "$name ${intTemp.toInt()}°\n$desc"

                    resultWeather?.text = l
                }
            }
        }
    }

    private fun initGoogleMap(savedInstanceState: Bundle?){
        var mapViewBundle : Bundle? = null
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey")
        }
        mMapView.onCreate(mapViewBundle)
        mMapView.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle("MapViewBundleKey")
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle("MapViewBundleKey", mapViewBundle)
        }
        mMapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mMapView.onStop()
    }

    override fun onPause() {
        mMapView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mMapView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMapView.onLowMemory()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onMapReady(map: GoogleMap) {
        map.setOnMapClickListener {
            map.clear()
            map.animateCamera(CameraUpdateFactory.newLatLng(it))
            newProductLocation = LatLng(it.latitude,it.longitude)
            map.addMarker(MarkerOptions().position(newProductLocation!!))
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            return
        }
        map.isMyLocationEnabled = true
        if(oldProductLocation != null)
            map.addMarker(MarkerOptions().position(oldProductLocation!!))
    }
}