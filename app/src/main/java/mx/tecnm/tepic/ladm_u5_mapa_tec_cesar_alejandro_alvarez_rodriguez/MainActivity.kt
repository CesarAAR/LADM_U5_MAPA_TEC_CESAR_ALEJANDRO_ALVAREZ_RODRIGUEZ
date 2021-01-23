package mx.tecnm.tepic.ladm_u5_mapa_tec_cesar_alejandro_alvarez_rodriguez

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var posicion = ArrayList<Data>()
    lateinit var location : LocationManager //El lateinit var es para poner un valor a la variable despues.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),1)
        }//fin if permisos

        baseRemota.collection("tecnologico")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if(firebaseFirestoreException != null){
                    textView1.setText("ERROR: "+firebaseFirestoreException.message)
                    return@addSnapshotListener
                }

                var resultado = ""
                posicion.clear()
                for(document in querySnapshot!!){
                    var data = Data()
                    data.nombre = document.getString("nombre").toString()
                    data.posicion1 = document.getGeoPoint("posicion1")!!
                    data.posicion2 = document.getGeoPoint("posicion2")!!

                    resultado+= data.toString()+"\n\n"
                    posicion.add(data)
                }
                textView1.setText(resultado)
            }

        location = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var oyente = Oyente(this)
        location.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,01F,oyente)
        //01F ES PARA INDICAR QUE NO QUIERO MINUTOS DE DISTANCIA
    }
}///fin

class Oyente(puntero:MainActivity): LocationListener {
    var p = puntero
    var n = "Centro de Informacion"
    override fun onLocationChanged(location: Location) {
        p.textView2.setText("${location.latitude},${location.longitude}")
        p.textView3.setText("")
        var geoPosicionGPS = GeoPoint(location.latitude,location.longitude)

        for(item in p.posicion){
            if(item.estoyEn(geoPosicionGPS)){
                if(item.nombre==n){
                    p.textView3.setText("Estas en: ${item.nombre}\nContiene: " +
                            "\nSala de proyeccion, biblio, centro copiado, hemeroteca, baños")
                }else{
                    p.textView3.setText("Estas en: ${item.nombre}")
                }
            }
        }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }


}