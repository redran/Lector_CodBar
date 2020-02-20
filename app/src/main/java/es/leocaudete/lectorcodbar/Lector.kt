package es.leocaudete.lectorcodbar

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import es.leocaudete.lectorcodbar.Utils.GestionPermisos
import kotlinx.android.synthetic.main.activity_lector.*


class Lector : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_CODE = 234
    private val CODIGO_INTENT=1
    private lateinit var gestionPermisos: GestionPermisos
    private var leidos: MutableList<String> = mutableListOf()
    private var listaSumados: HashMap<String,Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lector)
    }

    // Anulamos la opción de volver a tras a través del botón del móvil
    override fun onBackPressed() {
        //
    }

    /*
 Infla el menú
  */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.lector_menu, menu)
        return true
    }

    // Accion que lleva a cabo el botón al ser pulsado
    fun escanea(view: View) {
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d("DEBUG", "El permiso ya está concedido")
            val i = Intent(this, Escanear::class.java)
            startActivityForResult(i, CODIGO_INTENT)
        }else{
            gestionPermisos = GestionPermisos(this,android.Manifest.permission.CAMERA,MY_PERMISSIONS_REQUEST_CODE)
            gestionPermisos.checkPermissions()
        }
    }

    // Al volver de la camara, mostramos el codigo leido
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==CODIGO_INTENT){
            if(resultCode==Activity.RESULT_OK){
                if(data!=null){
                    val codigo = data?.getStringExtra("codigo")

                    leidos.add(codigo) // añadimos el codigo a la lista que va a formar nuestro txt

                    // Comprobamos si el codigo existe en el HashMap
                    if(listaSumados.containsKey(codigo)){
                        val valor= listaSumados[codigo]
                        listaSumados[codigo] = valor!!.plus(1) // Si existe le sumamos uno
                    }else{
                        listaSumados[codigo]=1 // Sino existe lo añadimos
                    }
                    tv_codigo.text = listaSumados.toString()
                }
            }
        }
    }


}
