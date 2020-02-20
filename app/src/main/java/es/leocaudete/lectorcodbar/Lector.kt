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
import androidx.recyclerview.widget.LinearLayoutManager
import es.leocaudete.lectorcodbar.modelo.Linea
import es.leocaudete.lectorcodbar.utils.GestionPermisos
import kotlinx.android.synthetic.main.activity_lector.*
import kotlinx.android.synthetic.main.lista_recycled_view.*


class Lector : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_CODE = 234
    private val CODIGO_INTENT = 1

    private val myAdapter: RecyclerAdapter = RecyclerAdapter()
    private lateinit var gestionPermisos: GestionPermisos
    private var lineasParaTxt: MutableList<String> = mutableListOf()
    private var lineas: MutableList<Linea> = mutableListOf()

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

    private fun setUpRecyclerView() {


        listadoLineas.setHasFixedSize(true)
        listadoLineas.layoutManager = LinearLayoutManager(this)
        myAdapter.RecyclerAdapter(lineas, this)
        listadoLineas.adapter = myAdapter

    }

    // Accion que lleva a cabo el botón al ser pulsado
    fun escanea(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("DEBUG", "El permiso ya está concedido")
            val i = Intent(this, Escanear::class.java)
            startActivityForResult(i, CODIGO_INTENT)
        } else {
            gestionPermisos = GestionPermisos(
                this,
                android.Manifest.permission.CAMERA,
                MY_PERMISSIONS_REQUEST_CODE
            )
            gestionPermisos.checkPermissions()
        }
    }

    // Comprueba si ya existe un objeto repetido.
    // Si existe aumenta en 1 su cantidad
    // si no existe añada uno nuevo con el codigo pasado por parametro
    private fun preProcesoLinea(codigo: String) {


        var encontrado = false
        // Buscamos si ya existe el registro y si es asi entonces solo aumentamos la cantidad
        // Asi no hay lineas con codigo repetido
        for (linea in lineas) {
            if (linea.codigo.equals(codigo)) {
                linea.cantidad += 1
                encontrado = true
            }
        }
        if (!encontrado || lineas.isEmpty()) {
            val nuevaLinea = Linea()
            nuevaLinea.codigo = codigo
            nuevaLinea.cantidad = 1
            lineas.add(nuevaLinea)
        }
        //  btRestar.isEnabled = true
    }


    // Al volver de la camara, mostramos el codigo leido
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_INTENT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val codigo = data?.getStringExtra("codigo")
                    preProcesoLinea(codigo)
                    lineasParaTxt.add(codigo)
                    setUpRecyclerView()
                }
            }
        }
    }


}
