package com.hansung.capstone

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
//import com.google.android.gms.location.*
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.CameraPosition
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MarkerOptions

//class RidingActivity : AppCompatActivity(), OnMapReadyCallback {
class RidingActivity : AppCompatActivity() {

    lateinit var locationPermission: ActivityResultLauncher<Array<String>>
//    private lateinit var mMap: GoogleMap
//    private lateinit var fusedLocationClient: FusedLocationProviderClient // 위칫값 사용을 위해 필요
//    private lateinit var locationCallback: LocationCallback // 위칫값 요청에 대한 갱신 정보를 받는 데 필요

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riding)
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)

//        locationPermission =
//            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
//                if (results.all { it.value }) {
//                    startProcess()
//                } else {
//                    Toast.makeText(
//                        this, "권한 승인이 필요합니다.", Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//
//        locationPermission.launch(
//            arrayOf(
//                android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                android.Manifest.permission.ACCESS_FINE_LOCATION
//            )
//        )
    }
//
//    override fun onMapReady(googleMap: GoogleMap) {
//        mMap = googleMap
////        val gLatLng = LatLng(37.566418, 126.977943)
////        val markerOptions = MarkerOptions()
////            .position(gLatLng)
////            .title("Seoul City Hall")
////            .snippet("37.566418, 126.977943")
////        mMap.addMarker(markerOptions)
////        val cameraPosition = CameraPosition.Builder()
////            .target(gLatLng)
////            .zoom(15.0f)
////            .build()
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        updateLocation()
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                this,
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
////        mMap.isMyLocationEnabled = true
//
////        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
////        mMap.moveCamera(cameraUpdate)
//    }

//    @SuppressLint("MissingPermission")
//    fun updateLocation() {
//        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
////        locationRequest.run{
////            priority=Priority.PRIORITY_HIGH_ACCURACY
////            interval = 1000
////        }
//        locationCallback = object : LocationCallback() {
//            override fun onLocationResult(p0: LocationResult) {
//                p0.let {
//                    for ((i, location) in it.locations.withIndex()) {
//                        Log.d("Location", "$i ${location.latitude} , ${location.longitude}")
//                        setLastLocation(location)
//                    }
//                }
//            }
//        }
//        fusedLocationClient.requestLocationUpdates(
//            locationRequest,
//            locationCallback,
//            Looper.myLooper()
//        )
//    }
//
//    fun setLastLocation(lastLocation: Location) {
//        val gLATLNG = LatLng(lastLocation.latitude, lastLocation.longitude)
//        val markerOptions = MarkerOptions()
//            .position(gLATLNG)
//            .title("Here!")
//        val cameraPosition = CameraPosition.Builder()
//            .target(gLATLNG)
//            .zoom(15.0f)
//            .build()
//        mMap.clear()
////        mMap.addMarker(markerOptions)
//        mMap.moveCamera((CameraUpdateFactory.newCameraPosition(cameraPosition)))
//    }
//
//    private fun startProcess() {
//        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//
//    }
}