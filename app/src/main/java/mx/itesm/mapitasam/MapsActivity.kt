package mx.itesm.mapitasam

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import mx.itesm.mapitasam.databinding.ActivityMapsBinding
import com.google.android.gms.location.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val PERMISO_UBICACION = 0
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        // cambio de tipo entre tipos válidos
        // CASTING
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // podemos obtener la ubicación del usuario por medio de las fused location api

        // creamos un objeto de solicitud de ubicación
        var solicitud = LocationRequest.create().apply {

            interval = 10 * 1000
            fastestInterval = 2 * 1000
        }

        // solicitamos updates
        // agregamos verificacion de permisos
        if(ContextCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ){

            // si tengo autorizacion empiezo a tener updates
            LocationServices
                .getFusedLocationProviderClient(this)
                .requestLocationUpdates(
                    solicitud,
                    object: LocationCallback() {

                        override fun onLocationResult(p0: LocationResult) {
                            super.onLocationResult(p0)

                            Log.wtf("ACTUALIZACION", p0.lastLocation.toString())
                        }
                    },
                    Looper.myLooper()
                )
        }


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val edificio = LatLng(20.737030, -103.454188)
        mMap.addMarker(MarkerOptions().position(edificio).title("Salón de TC2007B"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(edificio, 18f))

        // en estricto - con esto se habilita la capita de ubicación en maps
        habilitarMyLocation()

        mMap.setOnMapClickListener { latLng ->

            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("marcador creado dinámicamente")
                    .alpha(0.5f)
                    .icon(
                        BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_ORANGE
                        )
                    )
            )
        }


    }

    fun habilitarMyLocation() {


        // aquí podemos verificar explícitamente los permisos
        if (
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION // COARSE
            )
            ==
            PackageManager.PERMISSION_GRANTED
        ) {
            // si lo tiene lo corremos
            mMap.isMyLocationEnabled = true

        } else {


            Toast.makeText(this, "PIDIENDO PERMISOS", Toast.LENGTH_SHORT).show()
            // si no lo tiene lo pedimos

            // solicitamos conjunto de permisos en un arreglo
            val permisos = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions(permisos, PERMISO_UBICACION)

        }

    }

    // método que se invoca al picar aceptar o rechazar permisos
    // en ventana emergente
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISO_UBICACION &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED){

            mMap.isMyLocationEnabled = true
        }
    }
}