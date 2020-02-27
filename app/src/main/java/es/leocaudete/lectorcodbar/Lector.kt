package es.leocaudete.lectorcodbar

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import es.leocaudete.lectorcodbar.adapters.RecyclerAdapter
import es.leocaudete.lectorcodbar.modelo.Equipo
import es.leocaudete.lectorcodbar.modelo.Linea
import es.leocaudete.lectorcodbar.utils.GestionPermisos
import es.leocaudete.lectorcodbar.utils.ShowMessages
import kotlinx.android.synthetic.main.activity_lector.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException


class Lector : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_CODE = 234
    private val CODIGO_ESCANEAR = 1
    private val CODIGO_GUARDAR = 2
    private lateinit var storageLocalDir: String

    private val myAdapter: RecyclerAdapter =
        RecyclerAdapter()
    private lateinit var gestionPermisos: GestionPermisos
    private var lineas = ArrayList<Linea>()

    private lateinit var gestorMensajes: ShowMessages


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lector)
        storageLocalDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()
        gestorMensajes = ShowMessages()

        lineas = intent.getSerializableExtra("lineas") as ArrayList<Linea>
        setUpRecyclerView()
    }

    // Opción de volver a tras a través del botón del móvil
    override fun onBackPressed() {
        gestorMensajes.showAlert(
            "Atención",
            "Estás a punto de salir y se perderán las lecturas realizadas. ¿Estás seguro?",
            this,
            { cancelar() })

    }

    /*
 Infla el menú
  */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.lector_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.cancelar -> {
                gestorMensajes.showAlert(
                    "Atención",
                    "Estás a punto de salir y se perderán las lecturas realizadas. ¿Estás seguro?",
                    this,
                    { cancelar() })
                true
            }

            R.id.limpiar -> {
                gestorMensajes.showAlert(
                    "Atención",
                    "Se van ha borrar todos los datos leidos. ¿Estás seguro?",
                    this,
                    { limpiar() })
                true

            }
            R.id.save -> {
                guardar()
                true
            }
            R.id.save_icon -> {
                guardar()
                true
            }
            R.id.send -> {
                enviar()
                true
            }
            R.id.send_icon -> {
                enviar()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun enviar() {
        if (lineas.size > 0) {
            val intent = Intent(this, Destinos::class.java).apply {
                putExtra("codigos", creaListaTxt())
            }
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "Antes de enviar al servidor agrega una lectura",
                Toast.LENGTH_LONG
            ).show()
        }


    }

    private fun limpiar() {

        lineas.clear()
        setUpRecyclerView()
    }

    private fun cancelar() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun guardar() {

        if (lineas.size > 0) {
            val intent = Intent(this, GuardarLectura::class.java)
            startActivityForResult(intent, CODIGO_GUARDAR)
        } else {
            Toast.makeText(this, "No se puede guardar un fichero sin líneas", Toast.LENGTH_LONG)
                .show()
        }
    }




    // Va a crear un ArrayList que contiene todas las líneas leidas
    private fun creaListaTxt(): ArrayList<String> {
        var lista = ArrayList<String>()

        for (linea in lineas) {
            /*for (i in 0 until linea.cantidad) {
                lista.add(linea.codigo)
            }*/
            lista.add(linea.codigo)
        }
        return lista
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
            startActivityForResult(i, CODIGO_ESCANEAR)
        } else {
            gestionPermisos = GestionPermisos(
                this,
                android.Manifest.permission.CAMERA,
                MY_PERMISSIONS_REQUEST_CODE
            )
            gestionPermisos.checkPermissions()
        }
    }

    private fun sobreEscribeLectura(index:Int, codigo:String){
        lineas[index].codigo=codigo
    }
    // Comprueba si ya existe un objeto repetido.
    // Si existe aumenta en 1 su cantidad
    // si no existe añada uno nuevo con el codigo pasado por parametro
    private fun preProcesoLinea(codigo: String) {


        var encontrado = false
        // Buscamos si ya existe el registro y si es asi entonces solo aumentamos la cantidad
        // Asi no hay lineas con codigo repetido
        for (linea in lineas) {
            if (linea.codigo.substring(5,11).equals(codigo.substring(5,11))) {
                gestorMensajes.showAlert(
                    "Atención",
                    "El paquete ya se ha leido. ¿Deseas sobreescribir la lectura?",
                    this,
                    { linea.codigo=codigo })
                encontrado=true
            }
        }
        if (!encontrado || lineas.isEmpty()) {
            val nuevaLinea = Linea()
            nuevaLinea.codigo = codigo
           // nuevaLinea.cantidad = 1
            lineas.add(nuevaLinea)
        }
        //  btRestar.isEnabled = true
    }


    // Al volver de la camara, mostramos el codigo leido
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_ESCANEAR) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val codigo = data?.getStringExtra("codigo")
                    preProcesoLinea(codigo)
                    setUpRecyclerView()
                }
            }
        }
        if (requestCode == CODIGO_GUARDAR) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val nombreFichero = data?.getStringExtra("nombre")

                    var fichero = File("$storageLocalDir/$nombreFichero")
                    if (!fichero.exists()) {
                        var ficheroSalida = FileOutputStream(fichero)
                        var ficheroObjetos = ObjectOutputStream(ficheroSalida)
                        for (ln in lineas) {
                            ficheroObjetos.writeObject(ln)
                        }
                        ficheroObjetos.close()
                        cancelar()
                    } else {
                        Toast.makeText(
                            this,
                            "Ya existe un fichero con ese nombre",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }
        }

    }


}
